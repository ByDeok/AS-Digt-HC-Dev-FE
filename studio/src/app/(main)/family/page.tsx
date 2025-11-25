// src/app/(main)/family/page.tsx
/**
 * 스크립트 용도: 가족 정보 및 활동 피드 페이지
 * 
 * 함수 호출 구조:
 * FamilyPage
 * ├── Header
 * ├── Card (Family Members)
 * │   ├── Avatar
 * │   └── Badge
 * └── Card (Activity Feed)
 *     └── Icons (HeartPulse, Pill)
 */

"use client";

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { mockFamilyMembers, mockActivityFeed, type Activity } from "@/lib/mockData";
import { Mail, HeartPulse, Pill, ChevronsRight } from "lucide-react";
import { useToast } from "@/hooks/use-toast";
import { PageHeader } from "@/components/common/PageHeader";
import { Section } from "@/components/common/Section";
import { ListItem } from "@/components/common/ListItem";

const activityIcons: { [key in Activity['type']]: React.ReactNode } = {
    'measure': <HeartPulse className="w-5 h-5" />,
    'medication': <Pill className="w-5 h-5" />
};

/**
 * 프로그램 단위 용도: 연결된 가족 멤버 목록과 그들의 최근 활동 내역을 표시
 * 기능:
 * - 가족 멤버 목록 렌더링 (역할 표시)
 * - 활동 피드 타임라인 표시
 * - 가족 초대 링크 복사 기능
 */
export default function FamilyPage() {
    const { toast } = useToast();

    const handleInvite = () => {
        navigator.clipboard.writeText("https://golden-wellness.app/invite/family-code-xyz");
        toast({
            title: "초대 링크가 복사되었습니다.",
            description: "가족에게 링크를 공유하여 초대하세요.",
        });
    };

    return (
        <div className="bg-gray-50 min-h-screen">
            <PageHeader title="가족" className="justify-center" />
            
            <main className="p-4 space-y-6">
                <Section
                    title="우리 가족"
                    description="가족의 건강 상태를 함께 확인해요."
                    action={
                        <Button onClick={handleInvite} size="sm" withIcon>
                            <Mail className="w-4 h-4" />
                            가족 초대
                        </Button>
                    }
                >
                    <div className="space-y-4">
                        {mockFamilyMembers.map(member => (
                            <ListItem
                                key={member.uid}
                                className="p-0 hover:bg-transparent"
                                start={
                                    <Avatar>
                                        <AvatarImage src={`https://i.pravatar.cc/150?u=${member.uid}`} />
                                        <AvatarFallback>{member.name.charAt(0)}</AvatarFallback>
                                    </Avatar>
                                }
                                middle={
                                    <div>
                                        <p className="font-semibold">{member.name}</p>
                                        <p className="text-sm text-muted-foreground">{member.relation}</p>
                                    </div>
                                }
                                end={
                                    <Badge variant={member.role === 'admin' ? 'default' : 'secondary'}>
                                        {member.role === 'admin' ? '관리자' : member.role === 'viewer' ? '뷰어' : '대상자'}
                                    </Badge>
                                }
                            />
                        ))}
                    </div>
                </Section>

                <Section
                    title="활동 피드"
                    description="가족의 최근 활동 내역입니다."
                >
                    <div className="space-y-6">
                        {mockActivityFeed.map(activity => (
                            <div key={activity.id} className="flex items-start gap-4">
                                <div className="bg-secondary p-3 rounded-full mt-1">
                                   {activityIcons[activity.type]}
                                </div>
                                <div className="flex-1">
                                    <p className="text-sm">
                                        <span className="font-semibold">
                                            {mockFamilyMembers.find(m => m.uid === activity.userId)?.name || '알 수 없음'}
                                        </span>
                                        님이 {activity.description}
                                    </p>
                                    <p className="text-xs text-muted-foreground">{activity.timestamp}</p>
                                </div>
                                {activity.details && (
                                     <div className="text-sm text-right">
                                        <p className="font-semibold">{activity.details}</p>
                                        <p className="text-xs text-muted-foreground">측정 결과</p>
                                     </div>
                                )}
                            </div>
                        ))}
                    </div>
                    <Button variant="ghost" className="w-full mt-6 text-primary hover:text-primary">
                        더보기 <ChevronsRight className="w-4 h-4 ml-1" />
                    </Button>
                </Section>
            </main>
        </div>
    );
}
