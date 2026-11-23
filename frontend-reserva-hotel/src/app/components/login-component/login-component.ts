import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { LoginService } from '../../services/login-service';
import { IconComponent } from '../../layout/icon/icon';

@Component({
  imports: [ReactiveFormsModule, IconComponent],
  selector: 'app-login-component',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrl: './login-component.css',
  templateUrl: './login-component.html',
})
export class LoginComponent {
  private frmBuilder = inject(FormBuilder);
  private loginService = inject(LoginService);
  private router = inject(Router);

  error = signal<string | null>(null);
  loading = signal<boolean>(false);
  verPassword = signal<boolean>(false);

  readonly anio = new Date().getFullYear();

  frmLogin = this.frmBuilder.nonNullable.group({
    username: ['admin', Validators.required],
    password: ['Admin2026*', Validators.required],
  });

  login(): void {
    if (this.frmLogin.invalid) {
      this.frmLogin.markAllAsTouched();
      return;
    }
    this.error.set(null);
    this.loading.set(true);

    this.loginService.login(this.frmLogin.getRawValue()).subscribe({
      next: () => this.router.navigate(['/']),
      error: (er: Error) => {
        this.error.set(er.message);
        this.loading.set(false);
      }
    });
  }
}
