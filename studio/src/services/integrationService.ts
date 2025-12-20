import api from '@/lib/api';
import { unwrapApiResponse } from '@/services/common';

export type DeviceStatus = 'PENDING' | 'ACTIVE' | 'REVOKED' | 'EXPIRED' | 'ERROR';

export type DeviceLink = {
  deviceId: string;
  vendor: string;
  deviceType: string;
  status: DeviceStatus;
  lastSyncAt: string | null;
  hasActiveConsent: boolean;
};

export type ConsentStatus = 'ACTIVE' | 'EXPIRED' | 'REVOKED';
export type ConsentSubjectType = 'DEVICE' | 'PORTAL' | 'FAMILY_BOARD' | 'DATA_PROCESSING';

export type ConsentScope = {
  dataTypes: string[];
  frequency: string;
  retentionPeriod?: string | null;
  sharingAllowed?: Record<string, boolean> | null;
};

export type Consent = {
  consentId: string;
  subjectType: ConsentSubjectType;
  subjectName: string;
  scope: ConsentScope | null;
  status: ConsentStatus;
  consentedAt: string;
};

export const integrationService = {
  listDevices: async (): Promise<DeviceLink[]> => {
    const res = await api.get('/v1/integration/devices');
    return unwrapApiResponse<DeviceLink[]>(res, '연동된 디바이스 목록을 불러오지 못했습니다.');
  },

  connectDevice: async (req: {
    vendor: string;
    deviceType: string;
    authCode: string;
    consentScope: ConsentScope;
  }): Promise<DeviceLink> => {
    const res = await api.post('/v1/integration/devices', req);
    return unwrapApiResponse<DeviceLink>(res, '디바이스 연동에 실패했습니다.');
  },

  disconnectDevice: async (deviceId: string): Promise<void> => {
    await api.delete(`/v1/integration/devices/${deviceId}`);
  },

  syncDevice: async (deviceId: string) => {
    const res = await api.post(`/v1/integration/devices/${deviceId}/sync`);
    return unwrapApiResponse(res, '디바이스 동기화에 실패했습니다.');
  },

  listConsents: async (): Promise<Consent[]> => {
    const res = await api.get('/v1/integration/consents');
    return unwrapApiResponse<Consent[]>(res, '동의 목록을 불러오지 못했습니다.');
  },

  revokeConsent: async (consentId: string, revokeReason?: string): Promise<void> => {
    await api.delete(`/v1/integration/consents/${consentId}`, {
      data: revokeReason ? { revokeReason } : undefined,
    });
  },
};


