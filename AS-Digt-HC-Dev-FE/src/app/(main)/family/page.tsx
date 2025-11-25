"use client";

import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { mockFamilyMembers, mockActivityFeed, FamilyMember, Activity } from "@/lib/mockData";
import { Mail, HeartPulse, Pill, ChevronsRight } from "lucide-react";
import { useToast } from "@/hooks/use-toast";

const activityIcons: { [key in Activity['type']]: React.ReactNode } = {
    'measure': <HeartPulse className="w-5 h-5" />,
    'medication': <Pill className="w-5 h-5" />
};

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
            <header className="p-4 bg-background border-b sticky top-0">
                <h1 className="text-xl font-bold text-center">가족</h1>
            </header>
            
            <main className="p-4 space-y-6">
                <Card>
                    <CardHeader className="flex flex-row items-center justify-between">
                        <div>
                            <CardTitle>우리 가족</CardTitle>
                            <CardDescription>가족의 건강 상태를 함께 확인해요.</CardDescription>
                        </div>
                         <Button onClick={handleInvite} size="sm" withIcon>
                            <Mail className="w-4 h-4" />
                            가족 초대
                        </Button>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-4">
                            {mockFamilyMembers.map(member => (
                                <div key={member.uid} className="flex items-center justify-between">
                                    <div className="flex items-center gap-3">
                                        <Avatar>
                                            <AvatarImage src={`https://i.pravatar.cc/150?u=${member.uid}`} />
                                            <AvatarFallback>{member.name.charAt(0)}</AvatarFallback>
                                        </Avatar>
                                        <div>
                                            <p className="font-semibold">{member.name}</p>
                                            <p className="text-sm text-muted-foreground">{member.relation}</p>
                                        </div>
                                    </div>
                                    <Badge variant={member.role === 'admin' ? 'default' : 'secondary'}>
                                        {member.role === 'admin' ? '관리자' : member.role === 'viewer' ? '뷰어' : '대상자'}
                                    </Badge>
                                </div>
                            ))}
                        </div>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader>
                        <CardTitle>활동 피드</CardTitle>
                        <CardDescription>가족의 최근 활동 내역입니다.</CardDescription>
                    </CardHeader>
                    <CardContent>
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
                    </CardContent>
                </Card>
            </main>
        </div>
    );
}
