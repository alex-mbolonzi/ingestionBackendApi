import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { take, map } from 'rxjs/operators';
import { DatasetTechAndAssayService } from 'app/entities/dataset-tech-and-assay/dataset-tech-and-assay.service';
import { IDatasetTechAndAssay, DatasetTechAndAssay } from 'app/shared/model/dataset-tech-and-assay.model';

describe('Service Tests', () => {
  describe('DatasetTechAndAssay Service', () => {
    let injector: TestBed;
    let service: DatasetTechAndAssayService;
    let httpMock: HttpTestingController;
    let elemDefault: IDatasetTechAndAssay;
    let expectedResult;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = {};
      injector = getTestBed();
      service = injector.get(DatasetTechAndAssayService);
      httpMock = injector.get(HttpTestingController);

      elemDefault = new DatasetTechAndAssay(0, 0, 'AAAAAAA');
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);
        service
          .find(123)
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: elemDefault });
      });

      it('should create a DatasetTechAndAssay', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault,
        );
        const expected = Object.assign({}, returnedFromService);
        service
          .create(new DatasetTechAndAssay(null))
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));
        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: expected });
      });

      it('should update a DatasetTechAndAssay', () => {
        const returnedFromService = Object.assign(
          {
            datasetTechAndAssayId: 1,
            techniqueAndAssay: 'BBBBBB',
          },
          elemDefault,
        );

        const expected = Object.assign({}, returnedFromService);
        service
          .update(expected)
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));
        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: expected });
      });

      it('should return a list of DatasetTechAndAssay', () => {
        const returnedFromService = Object.assign(
          {
            datasetTechAndAssayId: 1,
            techniqueAndAssay: 'BBBBBB',
          },
          elemDefault,
        );
        const expected = Object.assign({}, returnedFromService);
        service
          .query(expected)
          .pipe(
            take(1),
            map(resp => resp.body),
          )
          .subscribe(body => (expectedResult = body));
        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a DatasetTechAndAssay', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
