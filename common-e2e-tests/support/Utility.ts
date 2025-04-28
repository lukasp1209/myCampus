class Utility {
   static getBaseUrl(env){
        switch(env){
            case "local":
                return "http://localhost:8080";

            default:
                return "http://localhost:8080";
        }
    }
    static uniqueNumber(): string {
        const now = new Date();
        
        const year = now.getFullYear();
        const month = this.padZero(now.getMonth() + 1); // Months are 0-indexed
        const day = this.padZero(now.getDate());
        const hours = this.padZero(now.getHours());
        const minutes = this.padZero(now.getMinutes());
        const seconds = this.padZero(now.getSeconds());
    
        return `${year}${month}${day}${hours}${minutes}${seconds}`;
      }
      private static padZero(num: number): string {
        return num.toString().padStart(2, '0');
      }
}

export default Utility;