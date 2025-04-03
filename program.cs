using System;

class Program
{
    static void Main()
    {
        Console.WriteLine("Hast du Lust auf dieses Projekt? (Ja/Nein)");
        string antwort = Console.ReadLine()?.Trim().ToLower();

        if (antwort == "ja")
        {
            Console.WriteLine("Hurra! Das wird großartig!");
        }
        else if (antwort == "nein")
        {
            Console.WriteLine("Motivation ist der Schlüssel zum Erfolg! Denke daran, warum du dieses Projekt gestartet hast. Jeder Schritt bringt dich weiter voran. Du schaffst das!");
        }
        else
        {
            Console.WriteLine("Ich verstehe nicht ganz. Bitte antworte mit Ja oder Nein.");
        }
    }
}
