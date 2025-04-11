namespace mycampus_backend.Utilities
{
    public static class PasswordGenerator
    {
        private static readonly Random Random = new Random();

        public static string GeneratePassword()
        {
            const string chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
            return new string(Enumerable.Repeat(chars, 12).Select(s => s[Random.Next(s.Length)]).ToArray());
        }
    }    
}

