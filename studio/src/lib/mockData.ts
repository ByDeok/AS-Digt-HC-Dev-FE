// src/lib/mockData.ts
/**
 * 스크립트 용도: 개발 및 테스트를 위한 모의 데이터(Mock Data) 정의
 */

import { format, subDays, subHours, formatDistanceToNow } from 'date-fns';
import { ko } from 'date-fns/locale';

// User Profile
export interface User {
  uid: string;
  name: string; // "김철수"
  age: number;
  conditions: string[]; // ["고혈압", "당뇨"]
  role: 'senior' | 'caregiver';
  connectedDevices: string[]; // ["Galaxy Watch 6", "Omron BP"]
}

// Daily Mission
export interface Mission {
  id: string;
  title: string; // "아침 약 드시기"
  isCompleted: boolean;
  type: 'medication' | 'exercise' | 'measure';
  date: string; // "2025-11-20"
}

// Health Metrics (Report)
export interface HealthMetric {
  date: string;
  systolic?: number; // 수축기 혈압
  diastolic?: number; // 이완기 혈압
  glucose?: number; // 혈당
  steps?: number; // 걸음 수
  source: string; // "manual" | "device"
}

// Family Member
export interface FamilyMember {
    uid: string;
    name: string;
    relation: string; // "나", "어머니", "동생"
    role: 'admin' | 'subject' | 'viewer';
    avatarUrl?: string;
}

// Activity Feed
export interface Activity {
    id: string;
    userId: string;
    type: 'medication' | 'measure';
    description: string; // "아침 약을 복용하셨습니다.", "혈압을 측정하셨습니다."
    details?: string; // "120/80 mmHg"
    timestamp: string;
}


/**
 * 프로그램 단위 용도: 예시용 사용자 및 헬스케어 데이터 생성
 * 기능:
 * - 사용자 프로필, 미션, 건강 데이터(혈압, 혈당), 가족 멤버, 활동 로그의 Mock 객체 제공
 */

export const mockUser: User = {
    uid: 'user123',
    name: '김철수',
    age: 72,
    conditions: ['고혈압', '당뇨'],
    role: 'senior',
    connectedDevices: [],
}

export const mockMissions: Mission[] = [
    { id: 'mission1', title: '아침 약 드시기', isCompleted: false, type: 'medication', date: format(new Date(), 'yyyy-MM-dd') },
    { id: 'mission2', title: '혈압 측정하기', isCompleted: false, type: 'measure', date: format(new Date(), 'yyyy-MM-dd') },
    { id: 'mission3', title: '30분 걷기', isCompleted: false, type: 'exercise', date: format(new Date(), 'yyyy-MM-dd') },
];

export const mockHealthMetrics: HealthMetric[] = Array.from({ length: 90 }).map((_, i) => {
    const date = subDays(new Date(), 90 - i - 1);
    const isWeekend = date.getDay() === 0 || date.getDay() === 6;
    
    // Simulate slightly higher/lower readings on weekends or randomly
    const randomFactor = Math.random() - 0.5; // -0.5 to 0.5

    return {
        date: format(date, 'yyyy-MM-dd'),
        systolic: Math.round(125 + randomFactor * (isWeekend ? 30 : 20)), // 105-145
        diastolic: Math.round(80 + randomFactor * (isWeekend ? 20 : 15)), // 65-95
        glucose: Math.round(100 + randomFactor * (isWeekend ? 40 : 30)), // 80-120
        source: Math.random() > 0.3 ? 'device' : 'manual'
    };
});

// Add some specific out-of-range data
mockHealthMetrics[20].systolic = 145;
mockHealthMetrics[21].diastolic = 58;
mockHealthMetrics[50].systolic = 150;
mockHealthMetrics[55].glucose = 130;
mockHealthMetrics[80].glucose = 65;
mockHealthMetrics[85].systolic = 88;


export const mockFamilyMembers: FamilyMember[] = [
    { uid: 'user_me', name: '김보호', relation: '나', role: 'admin' },
    { uid: 'user_mother', name: '박영희', relation: '어머니', role: 'subject' },
    { uid: 'user_sister', name: '김영희', relation: '동생', role: 'viewer' },
];

export const mockActivityFeed: Activity[] = [
    { 
        id: 'act1', 
        userId: 'user_mother', 
        type: 'measure', 
        description: '혈압을 측정하셨습니다.', 
        details: '135/85 mmHg',
        timestamp: formatDistanceToNow(subHours(new Date(), 1), { addSuffix: true, locale: ko })
    },
    { 
        id: 'act2', 
        userId: 'user_mother', 
        type: 'medication', 
        description: '아침 약을 복용하셨습니다.', 
        timestamp: formatDistanceToNow(subHours(new Date(), 2), { addSuffix: true, locale: ko })
    },
     { 
        id: 'act3', 
        userId: 'user_mother', 
        type: 'measure', 
        description: '혈당을 측정하셨습니다.', 
        details: '140 mg/dL',
        timestamp: formatDistanceToNow(subHours(new Date(), 8), { addSuffix: true, locale: ko })
    },
    { 
        id: 'act4', 
        userId: 'user_mother', 
        type: 'medication', 
        description: '저녁 약을 복용하셨습니다.', 
        timestamp: formatDistanceToNow(subHours(new Date(), 13), { addSuffix: true, locale: ko })
    },
];
