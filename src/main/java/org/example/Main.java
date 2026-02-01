package org.example;

import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.terminal.Attributes;
import org.jline.utils.InfoCmp;
import org.jline.utils.NonBlockingReader;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//import org.jline.terminal.Terminal;
//import org.jline.terminal.TerminalBuilder;
//import org.jline.utils.InfoCmp;
//import org.jline.utils.NonBlockingReader;
//
//import java.io.IOException;
//import java.util.List;

enum StoryType {
    FUNNY("搞笑"),
    ADVENTURE("冒险"),
    LEARNING("学习");

    private final String description;

    StoryType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}

class Story {
    private final String title;
    private final String content;
    private int stars = 0;
    private final LocalDateTime createDate;
    private final StoryType storyType;

    public Story(String title, String content, StoryType storyType) {
        this.title = title;
        this.content = content;
        this.createDate = LocalDateTime.now();
        this.storyType = storyType;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int _stars) {
        stars = _stars;
    }

    public String getCreateDate() {
        // 格式化时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
        return createDate.format(formatter);
    }

    public StoryType getStoryType() {
        return storyType;
    }

    @Override
    public String toString() {
        return "Story Title: " + title + ", Type: " + storyType + ", Stars: " + stars + ", Created on: " + createDate;
    }
}

class Monster {
    private final String name;
    private final int age;
    private final ArrayList<Story> stories = new ArrayList<>();

    public Monster(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void addStory(Story story) {
        this.stories.add(story);
    }

    public ArrayList<Story> getStories() {
        return stories;
    }

    public void setStoryStars(int storyIndex, int stars) {
        stories.get(storyIndex).setStars(stars);
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getStoriesCount() {
        return stories.size();
    }

    public int getAllStars() {
        int totalStars = 0;
        for (Story story : stories) {
            totalStars += story.getStars();
        }
        return totalStars;
    }

    @Override
    public String toString() {
        return "Monster Name: " + name + ", Age: " + age + ", Stories Count: " + stories.size() + ", Total Stars: "
                + getAllStars();
    }
}

class UI {

    private static Terminal terminal;

    static {
        try {
            terminal = TerminalBuilder.builder().system(true).build();
        } catch (IOException e) {
            System.err.println("\033[31m菜单显示出错: " + e.getMessage() + "\033[0m");
        }
    }

    public static int showMenu(String title, List<String> options) {
        return showMenu(title, options, true);
    }

    public static int showMenu(String title, List<String> options, boolean clearScreen) {
        if (terminal == null)
            return -1;
        int selectedIndex = 0;

        Attributes originalAttributes = terminal.enterRawMode();
        try {
            NonBlockingReader reader = terminal.reader();
            terminal.puts(InfoCmp.Capability.cursor_invisible);

            // 对于不需要clear先打印一个空行分隔
            if (!clearScreen) {
                terminal.writer().println();
            }

            boolean firstRun = true;

            while (true) {
                if (clearScreen) {
                    terminal.puts(InfoCmp.Capability.clear_screen); // 清理
                    terminal.puts(InfoCmp.Capability.cursor_home); // 光标归位
                }
                if (!firstRun) {
                    // 移动光标回到开始位置,以免覆盖
                    terminal.writer().print("\033[" + (options.size() + 3) + "A");
                }

                firstRun = false;

                var writer = terminal.writer();
                writer.println("\033[44m=== " + title + " ===\033[0m"); // 蓝色标题

                for (int i = 0; i < options.size(); i++) {
                    if (i == selectedIndex) {
                        // 选中项：绿色高亮，前面加 >
                        writer.println("\033[32m > " + options.get(i) + "\033[0m");
                    } else {
                        // 未选中项：普通显示
                        writer.println("   " + options.get(i));
                    }
                }
                writer.println("使用 'w/s' 上下移动，'Enter' 确认\n");

                terminal.flush();

                // 读按键
                int input = reader.read();

                if (input == 'w' || input == 'W') {
                    if (selectedIndex > 0)
                        selectedIndex--;
                } else if (input == 's' || input == 'S') {
                    if (selectedIndex < options.size() - 1)
                        selectedIndex++;
                } else if (input == 13 || input == 10) {
                    // Enter
                    return selectedIndex;
                } else if (input == 'q') {
                    // 按 q 退出
                    return -1;
                }
            }
        } catch (IOException e) {
            // 打印红色
            readInput("\033[31m菜单显示出错: " + e.getMessage() + "\033[0m");
            return -1;
        } finally {
            terminal.setAttributes(originalAttributes);
            terminal.puts(InfoCmp.Capability.cursor_visible);
            terminal.flush();
        }
    }

    public static String readInput(String prompt) {
        if (terminal == null)
            return "";
        LineReader lineReader = LineReaderBuilder.builder().terminal(terminal).build();
        return lineReader.readLine(prompt);
    }

    public static void println(String message) {
        if (terminal == null)
            return;
        terminal.writer().println(message);
        terminal.flush();
    }

    public static void pause() {
        if (terminal == null)
            return;
        Attributes originalAttributes = terminal.enterRawMode();
        try {
            NonBlockingReader reader = terminal.reader();
            terminal.writer().println("\n按任意键继续...");
            terminal.flush();
            reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            terminal.setAttributes(originalAttributes);
        }
    }
}

public class Main {

    private static final ArrayList<Monster> monsters = new ArrayList<>();
    // private static final Scanner scanner = new Scanner(System.in);

    static void main(String[] args) {
        // 初始化测试数据
        initTestData();

        while (true)
            showStatistics();

    }

    private static void showStatistics() {
        ArrayList<String> monsterOptions = new ArrayList<>();

        int maxStars = 0;
        // 先计算所有怪兽中的最高分
        for (Monster m : monsters) {
            if (m.getAllStars() > maxStars) {
                maxStars = m.getAllStars();
            }
        }

        for (Monster monster : monsters) {
            String name = monster.getName();
            // 最高分，加上标记
            if (monster.getAllStars() == maxStars) {
                name += " \033[33m(最受欢迎)\033[0m";
            }
            monsterOptions.add(name + " (年龄: " + monster.getAge() + ", 故事数: " + monster.getStoriesCount() + ", 星星数："
                    + monster.getAllStars() + ")");
        }
        monsterOptions.add("添加新怪兽");

        int choice = UI.showMenu("怪兽统计", monsterOptions);

        // 选择查看详情
        if (choice >= 0 && choice < monsters.size()) {
            showMonsterDetail(monsters.get(choice));
        } else if (choice == monsters.size()) {
            showAddMonster();
            showStatistics();
        } else {
            UI.println("输入错误");
        }
    }

    private static void showAddMonster() {
        String name;
        int age;
        try {
            name = UI.readInput("请输入怪兽名称: ");
            age = Integer.parseInt(UI.readInput("请输入怪兽年龄: "));
        } catch (NumberFormatException e) {
            UI.println("\033[31m输入无效，添加怪兽失败！\033[0m");
            UI.pause();
            return;
        }
        Monster newMonster = new Monster(name, age);
        monsters.add(newMonster);
        System.out.println("怪兽添加成功！");
    }

    private static void showMonsterDetail(Monster monster) {
        UI.println("\n\033[44m======= 怪兽详情 =======\033[0m");
        UI.println("名称: " + monster.getName());
        UI.println("年龄: " + monster.getAge());
        UI.println("故事数量: " + monster.getStoriesCount());
        UI.println("故事总评分: " + monster.getAllStars());
        UI.println("\n--- 故事列表 ---");

        if (monster.getStories().isEmpty()) {
            UI.println("(无故事)");
        } else {
            String storyTable = AsciiTable.getTable(monster.getStories(), Arrays.asList(
                    new Column().header("标题").with(Story::getTitle),
                    new Column().header("类型").with(s -> s.getStoryType().toString()),
                    new Column().header("评分").with(s -> String.valueOf(s.getStars())),
                    new Column().header("创建时间").with(Story::getCreateDate),
                    new Column().header("内容").with(Story::getContent)

            ));
            UI.println(storyTable);
        }

        // 添加故事或返回主菜单
        int action = UI.showMenu("操作选择", Arrays.asList("添加新故事", "为故事设置评分", "返回主菜单"), false);
        switch (action) {
            case 0 -> showAddStory(monster);
            case 1 -> showSetStoryStar(monster);
            case 2 -> {
                // 返回主菜单
            }
            default -> {
                UI.println("\033[31m无效的操作选择！\033[0m");
                UI.pause();
            }
        }

    }

    private static void showSetStoryStar(Monster monster) {
        if (monster.getStories().isEmpty()) {
            UI.println("\033[31m该怪兽没有故事，无法设置评分！\033[0m");
            UI.pause();
            return;
        }

        ArrayList<String> storyOptions = new ArrayList<>();
        for (Story story : monster.getStories()) {
            storyOptions.add(story.getTitle() + " (当前评分: " + story.getStars() + ")");
        }

        int storyChoice = UI.showMenu("选择要评分的故事", storyOptions);
        if (storyChoice >= 0 && storyChoice < monster.getStories().size()) {
            String starInput = UI.readInput("请输入评分: ");
            try {
                int stars = Integer.parseInt(starInput);
                if (stars < 0) {
                    throw new NumberFormatException();
                }
                monster.setStoryStars(storyChoice, stars);
                UI.println("评分设置成功！");
            } catch (NumberFormatException e) {
                UI.println("\033[31m无效的评分输入\033[0m");
            }
        } else {
            UI.println("\033[31m无效的故事选择！\033[0m");
        }
        UI.pause();
    }

    private static void showAddStory(Monster monster) {
        String title = UI.readInput("请输入故事标题: ");
        int typeChoice = UI.showMenu("请选择故事类型", Arrays.asList("搞笑类", "冒险类", "学习类"), false);
        StoryType storyType;
        switch (typeChoice) {
            case 0 -> storyType = StoryType.FUNNY;
            case 1 -> storyType = StoryType.ADVENTURE;
            case 2 -> storyType = StoryType.LEARNING;
            default -> {
                UI.println("\033[31m无效的类型选择，默认设置为搞笑类\033[0m");
                storyType = StoryType.FUNNY;
            }
        }
        String content = UI.readInput("请输入故事内容: ");
        Story newStory = new Story(title, content, storyType);
        monster.addStory(newStory);
        UI.println("故事添加成功！");
        // 等待用户按键
        UI.pause();
    }

    private static void initTestData() {
        for (int i = 1; i <= 3; i++) {

            Monster testMonster = new Monster("Test Monster " + i, 5 + i * i);
            testMonster.addStory(new Story("Story 1", "1111111111111111111", StoryType.FUNNY));
            testMonster.addStory(new Story("Story 2", "222222222222222222222", StoryType.FUNNY));
            testMonster.setStoryStars(1, i);
            monsters.add(testMonster);

            // 输出测试数据
            System.out.println("生成测试数据: " + testMonster.toString());
            System.out.println("包含故事: ");
            for (Story story : testMonster.getStories()) {
                System.out.println(story.toString());
            }
        }
    }

}
