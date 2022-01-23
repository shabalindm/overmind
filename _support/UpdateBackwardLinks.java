import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UpdateBackwardLinks {

    public static final String linkSectionBeginLine = "\n# Обратные ссылки";

    public static void main(String[] args) throws Exception  {
        List<Path> allCardPaths;
        Path project = Paths.get("/git/overmind");
        try (Stream<Path> filepath = Files.walk(project)) {
            allCardPaths = filepath.filter(path -> path.getFileName().toString().endsWith(".md")).collect(Collectors.toList());
        }

        Map<Path, List<Card>> linkedCards = new HashMap<>();
        //scan
        for (Path cardPath : allCardPaths) {
            Path cardDir = project.relativize(cardPath).getParent();
            if (cardDir == null){
                cardDir = Path.of(".");
            }

            String content = getCardContent(cardPath);
            Pattern pattern = Pattern.compile("\\[.*\\]\\((.*\\.md)\\)");
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()){
                Path link = Path.of(matcher.group(1));
                Path linkedCardPath;
                if (link.isAbsolute())
                    linkedCardPath = project.resolve(link.toString().substring(1));
                else
                    linkedCardPath = project.resolve(cardDir).resolve(link);

                if(!linkedCardPath.toFile().exists()){
                    System.out.println("Card not exists: " + linkedCardPath);
                    continue;
                }
                linkedCardPath = linkedCardPath.normalize();

                String cardTitle =  content.substring(0, content.indexOf("\n"));
                if(!cardTitle.startsWith("#")){
                    cardTitle = cardPath.getFileName().toString();
                }
                else
                    cardTitle = cardTitle.substring(1).trim();

                linkedCards.computeIfAbsent(linkedCardPath, path -> new ArrayList<>()).add(new Card(cardTitle, cardPath));
            }
        }

        for (Path mdFile : allCardPaths) {
            List<Card> cards = linkedCards.get(mdFile);
            if (cards != null) {
                String content = getCardContent(mdFile);
                try (PrintStream out = new PrintStream(new FileOutputStream(mdFile.toFile()))) {
                    out.print(content);
                    if(!content.endsWith("\n"))
                       out.println();
                    out.println(linkSectionBeginLine);
                    for (Card card : cards) {
                        Path pathFromProjectRoot = project.relativize(card.path);
                        out.println("[" + card.title + "](/" + pathFromProjectRoot+")");
                        out.println();
                    }

                }



           }
        }
    }

    private static String getCardContent(Path card) throws IOException {
        String content = Files.readString(card);
        int endIndex = content.indexOf(linkSectionBeginLine);
        if (endIndex > 0)
            return content.substring(0, endIndex);
        return content;
    }

    private static class Card {
        String title;
        Path path;

        public Card(String title, Path path) {
            this.title = title;
            this.path = path;
        }
    }
}
