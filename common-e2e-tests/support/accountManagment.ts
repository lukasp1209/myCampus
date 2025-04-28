import fs from 'fs';
import path from 'path';

type Account = {
  email: string;
  password: string;
  lastname: string;
  timestamp?: number; // Added for tracking insertion order
};

type AccountDatabase = {
  students: Account[];
  professors: Account[];
};

const ACCOUNTS_FILE = path.join(__dirname, '../test-accounts.json');

// Initialize JSON file if it doesn't exist
function initializeAccountFile(): void {
  if (!fs.existsSync(ACCOUNTS_FILE)) {
    fs.writeFileSync(ACCOUNTS_FILE, JSON.stringify({
      students: [],
      professors: []
    }, null, 2));
  }
}

// Read the accounts file
function readAccounts(): AccountDatabase {
  initializeAccountFile();
  const data = fs.readFileSync(ACCOUNTS_FILE, 'utf-8');
  return JSON.parse(data) as AccountDatabase;
}

// Write to accounts file
function writeAccounts(data: AccountDatabase): void {
  fs.writeFileSync(ACCOUNTS_FILE, JSON.stringify(data, null, 2));
}

// Add a new account (now with timestamp)
export function addAccount(
  type: 'student' | 'professor',
  email: string,
  password: string,
  lastname: string
): void {
  const accounts = readAccounts();
  const newAccount: Account = { 
    email, 
    password, 
    lastname,
    timestamp: Date.now() 
  };

  if (type === 'student') {
    accounts.students.push(newAccount);
  } else {
    accounts.professors.push(newAccount);
  }

  writeAccounts(accounts);
}

// Get last added account of a type
export function getLastAddedAccount(type: 'student' | 'professor'): Account | null {
  const accounts = readAccounts();
  const targetArray = type === 'student' ? accounts.students : accounts.professors;
  
  if (targetArray.length === 0) return null;
  
  // Find account with highest timestamp
  return targetArray.reduce((latest, current) => 
    (!latest.timestamp || (current.timestamp && current.timestamp > latest.timestamp)) 
      ? current 
      : latest
  );
}

/* Existing functions remain the same */
export function getAccounts(type: 'student' | 'professor'): Account[] {
  const accounts = readAccounts();
  return type === 'student' ? accounts.students : accounts.professors;
}

export function getRandomAccount(type: 'student' | 'professor'): Account | null {
  const accounts = getAccounts(type);
  if (accounts.length === 0) return null;
  return accounts[Math.floor(Math.random() * accounts.length)];
}

export function getAccountByLastname(
  type: 'student' | 'professor',
  lastname: string
): Account | undefined {
  const accounts = getAccounts(type);
  return accounts.find(account => account.lastname === lastname);
}

export function clearAccounts(): void {
  writeAccounts({ students: [], professors: [] });
}