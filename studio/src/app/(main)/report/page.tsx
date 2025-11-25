// src/app/(main)/report/page.tsx
/**
 * 스크립트 용도: 건강 리포트 페이지 (차트 시각화)
 * 
 * 함수 호출 구조:
 * ReportPage
 * ├── Header (Report Title & Date)
 * ├── Buttons (Chart Toggle: BP / Glucose)
 * └── ResponsiveContainer (Recharts)
 *     └── LineChart
 *         ├── CustomDot (Custom Marker Logic)
 *         ├── ReferenceLine (Normal/Abnormal Thresholds)
 *         └── Tooltip/Legend
 */

"use client";

import { useState } from 'react';
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { mockUser, mockHealthMetrics } from "@/lib/mockData";
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, ReferenceLine } from 'recharts';
import { format, subMonths } from 'date-fns';
import { Printer } from 'lucide-react';
import { cn } from '@/lib/utils';
import { PageHeader } from '@/components/common/PageHeader';

/**
 * 프로그램 단위 용도: 차트 그래프의 데이터 포인트 커스텀 렌더링
 * 기능:
 * - 정상 범위를 벗어난 데이터 포인트를 빨간색으로 강조
 */
const CustomDot = (props: any) => {
  const { cx, cy, payload, dataKey } = props;

  let isOutOfRange = false;
  if (dataKey === 'systolic' && (payload.systolic > 140 || payload.systolic < 90)) {
    isOutOfRange = true;
  }
  if (dataKey === 'diastolic' && (payload.diastolic > 90 || payload.diastolic < 60)) {
    isOutOfRange = true;
  }
  if (dataKey === 'glucose' && (payload.glucose > 125 || payload.glucose < 70)) {
    isOutOfRange = true;
  }

  if (isOutOfRange) {
    return <circle cx={cx} cy={cy} r={5} fill="hsl(var(--destructive))" stroke="#fff" strokeWidth={2} />;
  }

  return <circle cx={cx} cy={cy} r={3} fill={props.stroke} />;
};


/**
 * 프로그램 단위 용도: 사용자의 건강 데이터(혈압, 혈당)를 그래프로 시각화하여 제공
 * 기능:
 * - 3개월 간의 건강 데이터 필터링 및 표시
 * - 혈압/혈당 그래프 전환
 * - 인쇄 기능 제공
 */
export default function ReportPage() {
  const [activeChart, setActiveChart] = useState<'bp' | 'glucose'>('bp');

  const handlePrint = () => {
    window.print();
  };

  const threeMonthsAgo = subMonths(new Date(), 3);
  const chartData = mockHealthMetrics.filter(d => new Date(d.date) >= threeMonthsAgo);

  const formattedData = chartData.map(d => ({
    ...d,
    formattedDate: format(new Date(d.date), 'MM/dd'),
  }));

  return (
    <div className="bg-gray-100 min-h-screen p-4 print:p-0 print:bg-white">
      <div className="max-w-4xl mx-auto">
        <div className="flex justify-end mb-4 print:hidden">
            <Button onClick={handlePrint} withIcon>
              <Printer className="w-4 h-4 mr-2" />
              PDF 저장 / 인쇄
            </Button>
        </div>
        
        <Card className="p-2 sm:p-6 print:border-none print:shadow-none" id="report-content">
            <PageHeader
                title={`${mockUser.name}님 건강 리포트`}
                className="rounded-t-lg print:bg-white bg-gray-50 border-b-0 pb-0"
                leftContent={null}
                rightContent={
                    <div className="text-right mt-4 sm:mt-0">
                        <p className="font-semibold">{mockUser.name} ({mockUser.age}세)</p>
                        <p className="text-sm text-gray-600">{mockUser.conditions.join(', ')}</p>
                    </div>
                }
            />
            <div className="px-4 sm:px-6 pb-4 bg-gray-50 print:bg-white">
                 <p className="text-gray-500">{format(threeMonthsAgo, 'yyyy.MM.dd')} - {format(new Date(), 'yyyy.MM.dd')} (최근 3개월)</p>
            </div>
            
            <main className="p-4 sm:p-6">
                <div className="print:hidden mb-6 flex justify-center">
                    <div className="inline-flex rounded-md shadow-sm">
                        <Button
                            onClick={() => setActiveChart('bp')}
                            className={cn(
                                "rounded-r-none",
                                activeChart === 'bp' ? "bg-primary text-primary-foreground" : "bg-white text-gray-700"
                            )}
                        >
                            혈압
                        </Button>
                        <Button
                            onClick={() => setActiveChart('glucose')}
                             className={cn(
                                "rounded-l-none",
                                activeChart === 'glucose' ? "bg-primary text-primary-foreground" : "bg-white text-gray-700"
                            )}
                        >
                            혈당
                        </Button>
                    </div>
                </div>

                <div className={cn("print-container", activeChart === 'glucose' && 'print:hidden')}>
                    <h2 className="text-xl font-semibold mb-4 text-center">혈압 추이</h2>
                    <ResponsiveContainer width="100%" height={300}>
                        <LineChart data={formattedData} margin={{ top: 5, right: 20, left: -10, bottom: 5 }}>
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis dataKey="formattedDate" />
                            <YAxis domain={[50, 160]} />
                            <Tooltip />
                            <Legend />
                            <ReferenceLine y={140} label={{ value: "고혈압", position: 'insideTopLeft', fill: 'hsl(var(--destructive))' }} stroke="hsl(var(--destructive))" strokeDasharray="3 3" />
                            <ReferenceLine y={90} label={{ value: "정상", position: 'insideTopLeft' }} stroke="hsl(var(--primary))" strokeDasharray="3 3" />
                            <ReferenceLine y={60} label={{ value: "저혈압", position: 'insideTopLeft', fill: 'hsl(var(--destructive))' }} stroke="hsl(var(--destructive))" strokeDasharray="3 3" />
                            <Line type="monotone" dataKey="systolic" name="수축기" stroke="hsl(var(--chart-1))" strokeWidth={2} dot={<CustomDot dataKey="systolic" />} activeDot={{ r: 6 }} />
                            <Line type="monotone" dataKey="diastolic" name="이완기" stroke="hsl(var(--chart-2))" strokeWidth={2} dot={<CustomDot dataKey="diastolic" />} activeDot={{ r: 6 }} />
                        </LineChart>
                    </ResponsiveContainer>
                </div>
                
                <div className={cn("mt-10 print-container", activeChart === 'bp' && 'print:hidden')}>
                    <h2 className="text-xl font-semibold mb-4 text-center">공복 혈당 추이</h2>
                    <ResponsiveContainer width="100%" height={300}>
                        <LineChart data={formattedData} margin={{ top: 5, right: 20, left: -10, bottom: 5 }}>
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis dataKey="formattedDate" />
                            <YAxis domain={[60, 180]}/>
                            <Tooltip />
                            <Legend />
                            <ReferenceLine y={126} label={{ value: "당뇨 의심", position: 'insideTopLeft', fill: 'hsl(var(--destructive))' }} stroke="hsl(var(--destructive))" strokeDasharray="3 3" />
                             <ReferenceLine y={100} label={{ value: "정상", position: 'insideTopLeft' }} stroke="hsl(var(--primary))" strokeDasharray="3 3" />
                            <ReferenceLine y={70} label={{ value: "저혈당 주의", position: 'insideTopLeft', fill: 'hsl(var(--destructive))' }} stroke="hsl(var(--destructive))" strokeDasharray="3 3" />
                            <Line type="monotone" dataKey="glucose" name="공복 혈당" stroke="hsl(var(--chart-4))" strokeWidth={2} dot={<CustomDot dataKey="glucose" />} activeDot={{ r: 6 }} />
                        </LineChart>
                    </ResponsiveContainer>
                </div>

            </main>
            
            <footer className="text-center p-6 text-gray-500 border-t mt-6">
                <p>의사 선생님께 보여주세요.</p>
            </footer>
        </Card>
      </div>

       <style dangerouslySetInnerHTML={{__html: `
        @media print {
          body * {
            visibility: hidden;
          }
          #report-content, #report-content * {
            visibility: visible;
          }
          #report-content {
            position: absolute;
            left: 0;
            top: 0;
            width: 100%;
          }
          .print-container {
             display: block !important;
             page-break-before: auto;
             page-break-inside: avoid;
          }
           @page {
            size: A4;
            margin: 20mm;
          }
        }
      `}} />

    </div>
  );
}
