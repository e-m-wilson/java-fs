import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { StudentCard } from './student-card';

describe('StudentCard', () => {
  let component: StudentCard;
  let fixture: ComponentFixture<StudentCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StudentCard],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(StudentCard);
  fixture.componentRef.setInput('student', { id: 1, email: 'student@example.com' });
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
