package io.zipcoder;

import io.zipcoder.utils.Item;
import io.zipcoder.utils.ItemParseException;
import io.zipcoder.utils.match.Match;
import io.zipcoder.utils.match.MatchGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemParser {
    Integer errorCounter = 0;


    public List<Item> parseItemList(String valueToParse) {
        List<String> jerkSONObjects = new ArrayList<>();
        List<Item> items = new ArrayList<>();

        Pattern pattern = Pattern.compile("##");
        Matcher matcher = pattern.matcher(valueToParse);
        MatchGroup group = new MatchGroup(matcher);

        Iterator<Match> iterator = group.iterator();
        Integer start = 0;
        while (iterator.hasNext()) {
            Match match = iterator.next();
            jerkSONObjects.add(valueToParse.substring(start, match.getEndingIndex()));
        }

        for (String string : jerkSONObjects)
            try {
                items.add(parseSingleItem(string));
            } catch (ItemParseException e) {
                errorCounter++;
            }
        return items;
    }

    public Item parseSingleItem(String item) throws ItemParseException {
        String name = stringParser("([nN][aA][mM][eE][:@^*%]\\w+;)", item, "(?1[:@^*%])\\w+(?=;)");
        String price = stringParser("([pP][rR][iI][cC][eE][:@^*%][0-9]\\.+[0-9][0-9])", item, "(?![:@^*%][0-9]\\.+[0-9] [0-9])");
        String type = stringParser("([tT][yY][pP][eE])[:@^*%]\\w+;)", item, "(?1[:@^*%])\\w+(?=;)");
        String expiration = stringParser("([eE][xX][pP][iI][rR][aA][tT][iI][oO][nN])[:@^*%]\\w+;)", item, "(?1[:@^*%])\\w+(?=;)");
        if (name != null && price != null && type != null && expiration != null) {
            return new Item(name.toLowerCase(), Double.valueOf(price), type.toLowerCase(), expiration);
        } else {
            throw new ItemParseException();
        }
    }

    ;

    private String stringParser(String regex, String text, String valueRegex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            Pattern valuePattern = Pattern.compile(valueRegex);
            Matcher valueMatcher = valuePattern.matcher(matcher.group());
            if (valueMatcher.find()) return valueMatcher.group();
        }
        return null;
    }
}
