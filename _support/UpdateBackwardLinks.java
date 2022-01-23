import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UpdateBackwardLinks {

    public static void main(String[] args) throws Exception  {
        List<Path> mdFiles;
        Path project = Paths.get("/git/overmind");
        try (Stream<Path> filepath = Files.walk(project)) {
            mdFiles = filepath.filter(path -> path.getFileName().toString().endsWith(".md")).collect(Collectors.toList());
        }

        Map<Path, List<Path>> reverseLinks = new HashMap<>();

        //scan
        String autoSectionBeginLine = "# Обратные ссылки";
        for (Path mdFile : mdFiles) {
            String name = mdFile.getFileName().toString();
            Path currentDir = project.relativize(mdFile).getParent();
            if (currentDir == null){
                currentDir = Path.of(".");
            }
            String content = Files.readString(mdFile).split(autoSectionBeginLine + "*$")[0];
            Pattern pattern = Pattern.compile("\\[.*\\]\\((.*\\.md)\\)");
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()){
                Path link = Path.of(matcher.group(1));
                Path linkedFile;
                if (link.isAbsolute())
                    linkedFile = project.resolve(link.toString().substring(1));
                else
                    linkedFile = project.resolve(currentDir).resolve(link);

                if(!linkedFile.toFile().exists()){
                    System.out.println("LinkedFile not Found: " + linkedFile);
                    continue;
                }
                reverseLinks.computeIfAbsent(linkedFile, path -> new ArrayList<>()).add(mdFile);
            }
        }

        for (Path mdFile : mdFiles) {
            if (reverseLinks.containsKey(mdFile)) {
                String content = Files.readString(mdFile).split(autoSectionBeginLine + "*$")[0];
                try (PrintStream out = new PrintStream(new FileOutputStream(mdFile.toFile()))) {
                    out.print(content);
                    out.println();
                    out.println(autoSectionBeginLine);

                }


           }
        }
    }
}
