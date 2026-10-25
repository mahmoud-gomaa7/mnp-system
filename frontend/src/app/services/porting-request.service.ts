
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export type PortingRequestStatus =
  | 'PENDING'
  | 'ACCEPTED'
  | 'REJECTED'
  | 'CANCELLED';

export interface PortingRequest {
  id: number;
  phoneNumber: string;
  donorOperator: string;
  recipientOperator: string;
  status: PortingRequestStatus;
  createdAt: string;
  decidedAt: string | null;
}


export interface PortingRequestPage {
  content: PortingRequest[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
}


export interface PhoneNumberInfo {
  phoneNumber: string;
  status: string;
  currentOperator: string;
}

export interface CreatePortingRequest {
  phoneNumber: string;
}

@Injectable({
  providedIn: 'root'
})
export class PortingRequestService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api';

  private createHeaders(organization: string): HttpHeaders {
    return new HttpHeaders({
      organization: organization
    });
  }

  
getRequests(
  organization: string,
  page: number = 0,
  size: number = 20
): Observable<PortingRequestPage> {
  return this.http.get<PortingRequestPage>(
    `${this.apiUrl}/porting-requests`,
    {
      headers: this.createHeaders(organization),
      params: {
        page,
        size
      }
    }
  );
}


  createRequest(
    organization: string,
    request: CreatePortingRequest
  ): Observable<PortingRequest> {
    return this.http.post<PortingRequest>(
      `${this.apiUrl}/porting-requests`,
      request,
      { headers: this.createHeaders(organization) }
    );
  }

  acceptRequest(
    organization: string,
    requestId: number
  ): Observable<PortingRequest> {
    return this.http.post<PortingRequest>(
      `${this.apiUrl}/porting-requests/${requestId}/accept`,
      {},
      { headers: this.createHeaders(organization) }
    );
  }

  rejectRequest(
    organization: string,
    requestId: number
  ): Observable<PortingRequest> {
    return this.http.post<PortingRequest>(
      `${this.apiUrl}/porting-requests/${requestId}/reject`,
      {},
      { headers: this.createHeaders(organization) }
    );
  }

  getPhoneNumber(
    organization: string,
    number: string
  ): Observable<PhoneNumberInfo> {
    return this.http.get<PhoneNumberInfo>(
      `${this.apiUrl}/phone-numbers/${number}`,
      { headers: this.createHeaders(organization) }
    );
  }
}
