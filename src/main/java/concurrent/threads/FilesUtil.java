package concurrent.threads;

import helper.PrintUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class FilesUtil {

    public static void createFile(final String name) {
        try {
            File file = new File(name);
            PrintUtils.printString("create file: " + file);
            if (!file.createNewFile()) {
                throw new IOException("File already created.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToFiles(final List<String> fileNames, final String URL) {
        List<File> files = fileNames.stream().map(File::new).collect(Collectors.toList());
        for (File file : files) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                PrintUtils.printString("write to file: " + file.getName());
                fos.write(URL.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void readFiles(final List<String> fileNames) {
        fileNames.forEach(file -> {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)
            )) {
                PrintUtils.printString("Read file: " + file);
                String line;
                while((line = br.readLine()) != null) {
                    PrintUtils.printString(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void deleteFiles(final List<String> fileNames) {
        fileNames.forEach(fileName -> {
            File file = new File(fileName);
            try {
                PrintUtils.printString("Delete file: " + file.toPath());
                Files.deleteIfExists(file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
