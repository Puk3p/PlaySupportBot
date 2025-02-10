package Puk3p.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StaffManager {
    private final File staffFile;
    private List<StaffMember> staffList;

    public StaffManager(File pluginDirectory) {
        if (!pluginDirectory.exists() && !pluginDirectory.mkdirs()) {
            throw new RuntimeException("❌ Nu s-a putut crea directorul principal al pluginului: " + pluginDirectory.getAbsolutePath());
        }

        this.staffFile = new File(pluginDirectory, "staff.json");

        if (staffFile.exists() && staffFile.isDirectory()) {
            System.err.println("⚠️ staff.json este un director. Îl ștergem pentru a putea crea fișierul.");
            if (!staffFile.delete()) {
                throw new RuntimeException("❌ Nu s-a putut șterge directorul staff.json!");
            }
        }

        this.staffList = new ArrayList<>();
        initializeStaffFile();
    }

    private void initializeStaffFile() {
        try {
            if (!staffFile.exists()) {
                if (staffFile.createNewFile()) {
                    System.out.println("✅ Fișierul staff.json a fost creat în directorul principal.");
                    staffList.add(new StaffMember("11111111111111", "Exemplu"));
                    saveStaff();
                } else {
                    throw new IOException("❌ Nu s-a putut crea fișierul staff.json.");
                }
            } else {
                loadStaff();
            }
        } catch (IOException e) {
            throw new RuntimeException("❌ Error initializing staff.json: " + e.getMessage(), e);
        }
    }

    public void loadStaff() {
        try (FileReader reader = new FileReader(staffFile)) {
            Type listType = new TypeToken<List<StaffMember>>() {}.getType();
            staffList = new Gson().fromJson(reader, listType);

            if (staffList == null) {
                staffList = new ArrayList<>();
            }

            System.out.println("✅ Fișierul staff.json a fost încărcat cu succes.");
        } catch (Exception e) {
            System.err.println("❌ Eroare la încărcarea fișierului staff.json: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveStaff() {
        try (FileWriter writer = new FileWriter(staffFile)) {
            Gson gson = new Gson().newBuilder().setPrettyPrinting().create();
            gson.toJson(staffList, writer);
            System.out.println("✅ Lista de staff a fost salvată cu succes.");
        } catch (IOException e) {
            System.err.println("❌ Eroare la salvarea fișierului staff.json: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<StaffMember> getStaffList() {
        return staffList;
    }

    public record StaffMember(String idDiscord, String nickname) {}
}
