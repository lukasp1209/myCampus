export interface UserLoginResponse {
  token: string;
  user: {
    id: string;
    email: string;
    role: string;
  };
}
