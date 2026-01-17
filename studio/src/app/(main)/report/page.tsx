// src/app/(main)/report/page.tsx
/**
 * 건강 리포트 페이지 (BE 연동 버전)
 *
 * 화면 이벤트 → 호출 API:
 * - 페이지 진입: GET /api/reports  (리포트 목록)
 * - "리포트 생성" 클릭: POST /api/reports/generate  (리포트 생성)
 * - "삭제" 클릭: DELETE /api/reports/{id}  (리포트 삭제)
 *
 * 참고:
 * - 현재 BE 리포트는 "기간 요약 metrics" 구조라, 기존의 일자별 혈압/혈당 시계열 차트 UI와 1:1 매칭되지 않습니다.
 * - 따라서 목록/상세(요약지표) 형태로 우선 연결하고, 시계열 API가 생기면 차트형으로 확장하는 것을 권장합니다.
 */

'use client';

import { useMemo, useState } from 'react';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { PageHeader } from '@/components/common/PageHeader';
import { Section } from '@/components/common/Section';
import { ListItem } from '@/components/common/ListItem';
import { LoadingSpinner } from '@/components/common/LoadingSpinner';
import { useToast } from '@/hooks/use-toast';
import { useDeleteReport, useGenerateReport, useReports } from '@/hooks/queries/useReports';
import type { HealthReport } from '@/services/reportsService';
import { Trash2 } from 'lucide-react';
import type { PeriodType } from '@/services/reportsService';

function formatRange(r: HealthReport) {
  return `${r.startDate} ~ ${r.endDate}`;
}

export default function ReportPage() {
  const { toast } = useToast();
  const [periodType, setPeriodType] = useState<PeriodType>('WEEKLY');
  const { data: reports = [], isLoading, isError } = useReports(periodType);
  const generateMutation = useGenerateReport(periodType);
  const deleteMutation = useDeleteReport(periodType);

  const [selectedId, setSelectedId] = useState<string | null>(null);

  const selected = useMemo(() => {
    if (!selectedId) return reports[0] || null;
    return reports.find((r) => r.reportId === selectedId) || null;
  }, [reports, selectedId]);

  const handleGenerate = async () => {
    try {
      const created = await generateMutation.mutateAsync();
      setSelectedId(created.reportId);
      toast({ title: '리포트 생성 완료', description: '새 리포트가 생성되었습니다.' });
    } catch (e) {
      const message = e instanceof Error ? e.message : '리포트 생성에 실패했습니다.';
      toast({ title: '리포트 생성 실패', description: message, variant: 'destructive' });
    }
  };

  const handlePeriodToggle = () => {
    setPeriodType((prev) => (prev === 'WEEKLY' ? 'MONTHLY' : 'WEEKLY'));
    setSelectedId(null); // Reset selection when changing period
  };

  const handleDelete = async (id: string) => {
    try {
      await deleteMutation.mutateAsync(id);
      toast({ title: '삭제 완료', description: '리포트가 삭제되었습니다.' });
      if (selectedId === id) setSelectedId(null);
    } catch (e) {
      const message = e instanceof Error ? e.message : '리포트 삭제에 실패했습니다.';
      toast({ title: '삭제 실패', description: message, variant: 'destructive' });
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-gray-50">
        <LoadingSpinner />
      </div>
    );
  }

  if (isError) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-gray-50">
        <p className="text-red-500">리포트를 불러오는 중 오류가 발생했습니다.</p>
      </div>
    );
  }

  return (
    <div className="bg-gray-50 min-h-screen">
      <PageHeader
        title="건강 리포트"
        rightContent={
          <div className="flex items-center gap-2">
            <Button variant={periodType === 'WEEKLY' ? 'default' : 'outline'} size="sm" onClick={handlePeriodToggle}>
              주간
            </Button>
            <Button variant={periodType === 'MONTHLY' ? 'default' : 'outline'} size="sm" onClick={handlePeriodToggle}>
              월간
            </Button>
            <Button onClick={handleGenerate} disabled={generateMutation.isPending}>
              {generateMutation.isPending ? '생성 중...' : '리포트 생성'}
            </Button>
          </div>
        }
      />

      <main className="p-4 space-y-6 max-w-3xl mx-auto w-full">
        <Section
          title="리포트 목록"
          description={`${periodType === 'WEEKLY' ? '주간' : '월간'} 리포트 목록입니다. 최근 생성된 리포트부터 표시됩니다.`}
        >
          {reports.length === 0 ? (
            <Card className="p-4 text-sm text-muted-foreground">리포트가 없습니다. 생성해보세요.</Card>
          ) : (
            <div className="space-y-3">
              {reports.map((r) => (
                <ListItem
                  key={r.reportId}
                  onClick={() => setSelectedId(r.reportId)}
                  className={selected?.reportId === r.reportId ? 'bg-primary/5 border-border' : 'bg-card'}
                  middle={
                    <div className="space-y-1">
                      <p className="font-semibold">{formatRange(r)}</p>
                      <p className="text-xs text-muted-foreground">
                        생성일: {r.createdAt ?? '알 수 없음'} · Report ID: {r.reportId}
                      </p>
                    </div>
                  }
                  end={
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={(e) => {
                        e.stopPropagation();
                        handleDelete(r.reportId);
                      }}
                      disabled={deleteMutation.isPending}
                      aria-label="리포트 삭제"
                    >
                      <Trash2 className="w-4 h-4" />
                    </Button>
                  }
                />
              ))}
            </div>
          )}
        </Section>

        <Section title="리포트 상세" description="선택한 리포트의 요약 지표입니다.">
          {!selected ? (
            <Card className="p-4 text-sm text-muted-foreground">표시할 리포트가 없습니다.</Card>
          ) : (
            <Card className="p-4 space-y-4">
              <div className="flex items-start justify-between gap-4">
                <div>
                  <p className="text-sm text-muted-foreground">기간</p>
                  <p className="text-lg font-semibold">{formatRange(selected)}</p>
                </div>
                <div className="text-right">
                  <p className="text-sm text-muted-foreground">생성일</p>
                  <p className="text-sm">{selected.createdAt ?? '-'}</p>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <Card className="p-3">
                  <p className="text-xs text-muted-foreground">걸음 수</p>
                  <p className="font-semibold">{selected.metrics?.activity?.steps ?? '-'}</p>
                </Card>
                <Card className="p-3">
                  <p className="text-xs text-muted-foreground">활동 시간(분)</p>
                  <p className="font-semibold">{selected.metrics?.activity?.activeMinutes ?? '-'}</p>
                </Card>
                <Card className="p-3">
                  <p className="text-xs text-muted-foreground">평균 심박수(bpm)</p>
                  <p className="font-semibold">{selected.metrics?.heartRate?.avgBpm ?? '-'}</p>
                </Card>
                <Card className="p-3">
                  <p className="text-xs text-muted-foreground">혈압(수축/이완)</p>
                  <p className="font-semibold">
                    {selected.metrics?.bloodPressure?.systolic ?? '-'} / {selected.metrics?.bloodPressure?.diastolic ?? '-'}
                  </p>
                </Card>
              </div>

              <div className="text-xs text-muted-foreground">
                데이터 출처: {selected.context?.deviceType ?? '-'} ({selected.context?.deviceId ?? '-'})
              </div>
            </Card>
          )}
        </Section>
      </main>
    </div>
  );
}


