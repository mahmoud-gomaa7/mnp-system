import { Component, OnInit, ChangeDetectorRef, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";

import {
  PortingRequestService,
  PortingRequest,
  PortingRequestPage,
  PhoneNumberInfo,
} from "./services/porting-request.service";

@Component({
  selector: "app-root",
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: "./app.html",
  styleUrl: "./app.css",
})
export class App implements OnInit {
  private readonly portingService = inject(PortingRequestService);
  private readonly cdr = inject(ChangeDetectorRef);

  operators = ["vodafone", "orange", "etisalat"];
  selectedOperator = "";

  phoneNumber = "";
  lookupNumber = "";

  requests: PortingRequest[] = [];
  phoneInfo: PhoneNumberInfo | null = null;

  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;

  loading = false;
  creatingRequest = false;
  errorMessage = "";
  successMessage = "";
  lookupError = "";
  requestActionError = "";
  loadRequestsError = "";

  processingRequestIds = new Set<number>();

  ngOnInit(): void {
    // Wait until the user selects an operator.
  }

  loadRequests(): void {
    if (!this.selectedOperator) {
      this.requests = [];
      this.totalPages = 0;
      this.totalElements = 0;
      this.loadRequestsError = "";
      this.requestActionError = "";
      return;
    }

    this.loading = true;
    this.loadRequestsError = "";
    this.requestActionError = "";

    this.portingService
      .getRequests(this.selectedOperator, this.currentPage, this.pageSize)
      .subscribe({
        next: (page: PortingRequestPage) => {
          this.requests = page.content;
          this.currentPage = page.number;
          this.totalPages = page.totalPages;
          this.totalElements = page.totalElements;
          this.loading = false;
          this.cdr.detectChanges();
        },
        error: (error) => {
          this.loadRequestsError =
            error?.error?.message ?? "Could not load porting requests.";

          this.requests = [];
          this.totalPages = 0;
          this.totalElements = 0;
          this.loading = false;

          this.cdr.detectChanges();
        },
      });
  }

  onOperatorChange(): void {
    this.currentPage = 0;
    this.phoneInfo = null;
    this.lookupError = "";
    this.errorMessage = "";
    this.successMessage = "";

    if (this.selectedOperator) {
      this.loadRequests();
    } else {
      this.requests = [];
    }
  }

  createRequest(): void {
    if (!this.selectedOperator || this.creatingRequest) {
      return;
    }

    this.errorMessage = "";
    this.successMessage = "";
    this.creatingRequest = true;

    this.portingService
      .createRequest(this.selectedOperator, {
        phoneNumber: this.phoneNumber,
      })
      .subscribe({
        next: (request) => {
          this.successMessage = `Porting request #${request.id} created successfully.`;
          this.phoneNumber = "";
          this.currentPage = 0;
          this.creatingRequest = false;
          this.loadRequests();
        },
        error: (error) => {
          this.errorMessage =
            error?.error?.message ??
            "Could not create the request. Please try again.";

          this.creatingRequest = false;
          this.cdr.detectChanges();
        },
      });
  }

  decideRequest(requestId: number, accept: boolean): void {
    if (this.processingRequestIds.has(requestId)) {
      return;
    }

    this.processingRequestIds.add(requestId);
    this.requestActionError = "";
    this.successMessage = "";

    const action = accept
      ? this.portingService.acceptRequest(this.selectedOperator, requestId)
      : this.portingService.rejectRequest(this.selectedOperator, requestId);

    action.subscribe({
      next: () => {
        this.successMessage = accept
          ? `Request #${requestId} accepted.`
          : `Request #${requestId} rejected.`;

        this.processingRequestIds.delete(requestId);
        this.loadRequests();
      },
      error: (error) => {
        this.requestActionError =
          error?.error?.message ?? "Could not update the request.";

        this.processingRequestIds.delete(requestId);
        this.cdr.detectChanges();
      },
    });
  }

  lookupPhoneNumber(): void {
    this.phoneInfo = null;
    this.lookupError = "";

    this.portingService
      .getPhoneNumber(this.selectedOperator, this.lookupNumber)
      .subscribe({
        next: (info) => {
          this.phoneInfo = info;
          this.cdr.detectChanges();

          console.log("Phone info assigned:", this.phoneInfo);
        },
        error: (error) => {
          console.error("Phone lookup failed:", error);

          this.lookupError =
            error?.error?.message ?? "Could not find the phone number.";

          this.cdr.detectChanges();
        },
      });
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadRequests();
    }
  }

  nextPage(): void {
    if (this.currentPage + 1 < this.totalPages) {
      this.currentPage++;
      this.loadRequests();
    }
  }
}
