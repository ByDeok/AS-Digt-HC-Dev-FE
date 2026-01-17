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

export type PortalStatus = 'PENDING' | 'ACTIVE' | 'FAILED' | 'UNSUPPORTED' | 'REVOKED';

export type PortalConnection = {
  portalId: string;
  portalType: string;
  portalName: string;
  status: PortalStatus;
  lastSyncAt: string | null;
};

export type PortalConnectRequest = {
  portalType: string;
  portalId?: string | null;
  credentials: Record<string, string>;
};

export type SyncResult = {
  recordsSynced: number;
  syncedAt: string;
  status: string;
  errors: string[];
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

  syncDevice: async (deviceId: string): Promise<SyncResult> => {
    const res = await api.post(`/v1/integration/devices/${deviceId}/sync`);
    return unwrapApiResponse<SyncResult>(res, '디바이스 동기화에 실패했습니다.');
  },

  listPortals: async (): Promise<PortalConnection[]> => {
    const res = await api.get('/v1/integration/portals');
    return unwrapApiResponse<PortalConnection[]>(res, '연동된 포털 목록을 불러오지 못했습니다.');
  },

  connectPortal: async (req: PortalConnectRequest): Promise<PortalConnection> => {
    const res = await api.post('/v1/integration/portals', req);
    return unwrapApiResponse<PortalConnection>(res, '포털 연동에 실패했습니다.');
  },

  syncPortal: async (portalId: string): Promise<SyncResult> => {
    const res = await api.post(`/v1/integration/portals/${portalId}/sync`);
    return unwrapApiResponse<SyncResult>(res, '포털 동기화에 실패했습니다.');
  },

  disconnectPortal: async (portalId: string): Promise<void> => {
    await api.delete(`/v1/integration/portals/${portalId}`);
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


