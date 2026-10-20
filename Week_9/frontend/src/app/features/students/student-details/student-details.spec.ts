import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { StudentDetails } from './student-details';

describe('StudentDetails', () => {
  let component: StudentDetails;
  let fixture: ComponentFixture<StudentDetails>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StudentDetails],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(StudentDetails);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
