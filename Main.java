import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.StringBuilder;

class ContactInfo {
    private String name;
    private String phoneNumber;
    private String email;

    // Constructor with full vars
    public ContactInfo(String name, String phoneNumber, String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    // Constructor with just 2 vars
    public ContactInfo(String name, String phoneNumber) {
        this(name, phoneNumber, "Unknown");
    }

    // getter - setter each vars
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    // Override toString method
    @Override
    public String toString() {
        return String.format("\n\t- Name: %s \n\t- Phone Number: %s \n\t- Email: %s", name, phoneNumber, email);
    }

    // Parse from string to object
    public static ContactInfo fromString(String inputString) {
        String[] attributes = inputString.split(",", 3); // because contacts have 3 attributes: name, phone, email
        if (attributes.length == 3) return new ContactInfo(attributes[0], attributes[1], attributes[2]);
        else return null;
    }

    // Convert object to String
    public String toOneLineString() {
        return name + "," + phoneNumber + "," + email;
    }
}

class ContactManager {
    private ArrayList<ContactInfo> contacts;

    // Constructor
    public ContactManager() throws IOException {
        this.contacts = new ArrayList<ContactInfo>();
        this.readDataTxt("data/contacts.txt");
    }

    // Read data contacts.txt
    private void readDataTxt(String fileName) throws IOException{
        File file = new File(fileName);
        if (file.exists()) {
            BufferedReader readFile = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = readFile.readLine()) != null) {
                ContactInfo existedContact = ContactInfo.fromString(line);
                if (existedContact != null) contacts.add(existedContact);
            }
        }
    }

    // getter
    public ArrayList<ContactInfo> getContacts() {
        return contacts;
    }

    // Check whether the phone number already exists.
    public boolean isExistedPhoneNumber(String phoneNumber) {
        for (ContactInfo contactInfo : contacts) {
            if (contactInfo.getPhoneNumber().equals(phoneNumber)) return true;
        }
        return false;
    }

    // Add a new contact
    public void addContact(String name, String phoneNumber) {
        ContactInfo newContact = new ContactInfo(name, phoneNumber);
        contacts.add(newContact);
        this.sortContactsAscending();
    }

    public void addContact(String name, String phoneNumber, String email) {
        ContactInfo newContact = new ContactInfo(name, phoneNumber, email);
        contacts.add(newContact);
        this.sortContactsAscending();
    }

    private void sortContactsAscending() {
        ArrayList<ContactInfo> newList = new ArrayList<>();
        ArrayList<String> allName = new ArrayList<>();
        for (ContactInfo contact : contacts) {
            allName.add(contact.getName());
        }
        allName.sort(String.CASE_INSENSITIVE_ORDER);
        for (String name : allName) {
            for (ContactInfo contact : contacts) {
                if (name.equals(contact.getName())) newList.add(contact);
            }
        }
        contacts = newList;
    }

    // Delete contact by phone number
    public void delContact(String phoneNumber) {
        boolean isDel = false;
        Iterator<ContactInfo> iterator = contacts.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getPhoneNumber().equals(phoneNumber)) {
                iterator.remove();
                isDel = true;
                break;
            }
        }
        if (!isDel) System.out.println("Don't have this contact!");
        else System.out.println("Contact has been deleted!");
    }

    // Find contact
    public ArrayList<ContactInfo> searchContactsByPhoneNumber(String phoneNumber) {
        // Search contacts by partial or full phone number.
        ArrayList<ContactInfo> searchContacts = new ArrayList<>();
        for (ContactInfo contact : contacts) if (contact.getPhoneNumber().contains(phoneNumber)) searchContacts.add(contact);
        if (searchContacts.isEmpty()) return null;
        else return searchContacts;
    }
    public ArrayList<ContactInfo> searchContactsByName(String name) {
        // Search contacts by partial or full name.
        ArrayList<ContactInfo> searchContacts = new ArrayList<>();
        for (ContactInfo contact : contacts) if (contact.getName().toLowerCase().contains(name.toLowerCase())) searchContacts.add(contact);
        if (searchContacts.isEmpty()) return null;
        else return searchContacts;
    }

    // Show all contacts
    public String showContacts() {
        StringBuilder sb = new StringBuilder();
        // Empty contact
        if (contacts.isEmpty()) return "You have no contact!";

        // Have contact(s)
        for (int i = 0; i < contacts.size(); i++) {
            sb.append(String.format("\n\tContact %d:", i+1));
            sb.append("\n\t\t- Name: ").append(contacts.get(i).getName());
            sb.append("\n\t\t- Phone Number: ").append(contacts.get(i).getPhoneNumber());
            sb.append("\n\t\t- Email: ").append(contacts.get(i).getEmail());
        }
        return sb.toString();
    }

    // Save contacts.txt
    public void saveContacts() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/contacts.txt"))) {
            for (ContactInfo contact : contacts) {
                writer.write(contact.toOneLineString());
                writer.newLine();
            }
            System.out.println("Save successful!");
        }
    }
}

public class Main {
    public static void main(String[] args) throws IOException {
        ContactManager contactManager = new ContactManager();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        boolean isEnd = false;
        while (!isEnd) {
            System.out.println("\n1. Add Contact");
            System.out.println("2. Delete Contact");
            System.out.println("3. Find contact");
            System.out.println("4. Display All Contacts");
            System.out.println("q. Quit.");
            System.out.print("Your choice: ");
            String choice = br.readLine();
            switch (choice) {
                case "1":
                    System.out.print("Enter Phone Number: ");
                    String phoneNumber = br.readLine();
                    if (!contactManager.isExistedPhoneNumber(phoneNumber)) {
                        System.out.print("Enter Contact Name: ");
                        String name = br.readLine();

                        System.out.print("Do you want to enter email? (y/n) ");
                        String decide = br.readLine();
                        if (decide.equalsIgnoreCase("y")) {
                            System.out.print("Enter Email: ");
                            String email = br.readLine();
                            contactManager.addContact(name, phoneNumber, email);
                        } else contactManager.addContact(name, phoneNumber);
                    } else System.out.printf("Phone number %s already exists!%n", phoneNumber);
                    break;
                case "2":
                    System.out.print("Enter phone number you want to delete: ");
                    String delPhoneNumber = br.readLine();

                    System.out.print("Are you sure you want to delete this contact (y/n)? ");
                    String delDecide = br.readLine();
                    if (delDecide.equalsIgnoreCase("y")) contactManager.delContact(delPhoneNumber);
                    else System.out.println("Stop deletion!");
                    break;
                case "3":
                    System.out.print("Do you want to search by name (1) or by phone number (2)? ");
                    String choiceSearch = br.readLine();
                    ArrayList<ContactInfo> searchList = new ArrayList<>();

                    // Search by name or phone number
                    boolean validChoice = false;
                    switch (choiceSearch) {
                        case "1":
                            System.out.print("Enter name you want to search contact: ");
                            searchList.addAll(contactManager.searchContactsByName(br.readLine()));
                            validChoice = true;
                            break;
                        case "2":
                            System.out.print("Enter phone number you want to search contact: ");
                            searchList.addAll(contactManager.searchContactsByPhoneNumber(br.readLine()));
                            validChoice = true;
                            break;
                        default:
                            System.out.println("Invalid choice!");
                    }

                    // Process after searching successfully.
                    if (validChoice) {
                        if (searchList == null) System.out.println("Contact is not found!");
                        else {
                            System.out.println("Found contacts:");
                            for (int i = 0; i < searchList.size(); i++) {
                                System.out.println("\tContact: " + i);
                                System.out.println("\t\t- " + searchList.get(i).getName());
                                System.out.println("\t\t- " + searchList.get(i).getPhoneNumber());
                                System.out.println("\t\t- " + searchList.get(i).getEmail());
                            }
                        }
                    }
                    break;
                case "4":
                    System.out.print("All contacts: ");
                    System.out.println(contactManager.showContacts());
                    break;
                case "q":
                    System.out.print("Are you sure you want to quit? (y/n): ");
                    String decideExit = br.readLine();
                    if (decideExit.equalsIgnoreCase("y")) {
                        isEnd = true;
                        contactManager.saveContacts();
                    }
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
