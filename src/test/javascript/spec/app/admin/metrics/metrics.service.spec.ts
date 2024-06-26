import { TestBed } from '@angular/core/testing';

import { JhiMetricsService } from 'app/admin/metrics/metrics.service';
import { SERVER_API_URL } from 'app/app.constants';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

describe('Service Tests', () => {
  describe('Logs Service', () => {
    let service: JhiMetricsService;
    let httpMock;
    let expectedResult;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });

      expectedResult = {};
      service = TestBed.get(JhiMetricsService);
      httpMock = TestBed.get(HttpTestingController);
    });

    afterEach(() => {
      httpMock.verify();
    });

    describe('Service methods', () => {
      it('should call correct URL', () => {
        service.getMetrics().subscribe(() => {});

        const req = httpMock.expectOne({ method: 'GET' });
        const resourceUrl = SERVER_API_URL + 'management/jhimetrics';
        expect(req.request.url).toEqual(resourceUrl);
      });

      it('should return Metrics', () => {
        const metrics = [];

        service.getMetrics().subscribe(received => {
          expectedResult = received;
        });

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(metrics);
        expect(expectedResult).toEqual(metrics);
      });

      it('should return Thread Dump', () => {
        const dump = [{ name: 'test1', threadState: 'RUNNABLE' }];

        service.threadDump().subscribe(received => {
          expectedResult = received;
        });

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(dump);
        expect(expectedResult).toEqual(dump);
      });
    });
  });
});
