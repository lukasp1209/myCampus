namespace mycampus_backend.Models
{
    public class ApiResponse<T>
    {
        public Metadata Metadata { get; set; }
        public T Payload { get; set; }
    }

    public class Metadata
    {
        public string Status { get; set; }
        public string Message { get; set; }
    }
}
