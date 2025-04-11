# Build stage
FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src

# Copy the main project file first to restore dependencies
COPY ["mycampus-backend.api.csproj", "./"]
RUN dotnet restore "mycampus-backend.api.csproj"

# Copy the entire solution
COPY . .

# Build the application
RUN dotnet build "mycampus-backend.api.csproj" -c Release -o /app/build

# Publish the application
RUN dotnet publish "mycampus-backend.api.csproj" -c Release -o /app/publish

# Runtime stage
FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS runtime
WORKDIR /app

# Copy the published application
COPY --from=build /app/publish .

# Set environment variables
ENV ASPNETCORE_ENVIRONMENT=Development
ENV ASPNETCORE_URLS=http://+:80

EXPOSE 80

ENTRYPOINT ["dotnet", "mycampus-backend.api.dll"]