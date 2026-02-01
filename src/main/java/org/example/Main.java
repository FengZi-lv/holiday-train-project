package org.example;

import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static java.lang.System.exit;


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
    private final int stars = 0;
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

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getStoriesCount() {
        return stories.size();
    }

    @Override
    public String toString() {
        return "Monster Name: " + name + ", Age: " + age + ", Stories Count: " + stories.size();
    }
}


public class Main {

    private static final ArrayList<Monster> monsters = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);


    static void main(String[] args) {
        // 初始化测试数据
        initTestData();

//        while (true)
        showStatistics();

    }


    private static void showStatistics() {
        System.out.println("\033[44m======= 怪兽统计 =======\033[0m");
        // 打印表格
        String table = AsciiTable.getTable(monsters, Arrays.asList(
                new Column().header("序号").with(m -> String.valueOf(monsters.indexOf(m) + 1)),
                new Column().header("\033[32m名称\033[0m").with(Monster::getName),
                new Column().header("\033[32m年龄\033[0m").with(m -> String.valueOf(m.getAge())),
                new Column().header("\033[32m故事数量\033[0m").with(m -> String.valueOf(m.getStoriesCount()))));
        System.out.println(table);

        // 选择查看详情
        System.out.print("请选择要查看详情的怪兽序号 (输入0添加怪兽): ");
        int choice = scanner.nextInt();
        if (choice == 0) {
                showAddMonster();
            } else if (choice > 0 && choice <= monsters.size()) {
            showMonsterDetail(monsters.get(choice - 1));
        } else {
            System.out.println("无效的选择，请重试");
        }
    }

    private static void showAddMonster() {
        scanner.nextLine(); // 清除换行符
        System.out.print("请输入怪兽名称: ");
        String name = scanner.nextLine();
        System.out.print("请输入怪兽年龄: ");
        int age = scanner.nextInt();

        Monster newMonster = new Monster(name, age);
        monsters.add(newMonster);
        System.out.println("怪兽添加成功！");
    }

    private static void showMonsterDetail(Monster monster) {
        System.out.println("\n\033[44m======= 怪兽详情 =======\033[0m");
        System.out.println("名称: " + monster.getName());
        System.out.println("年龄: " + monster.getAge());
        System.out.println("故事数量: " + monster.getStoriesCount());
        System.out.println("\n--- 故事列表 ---");

        if (monster.getStories().isEmpty()) {
            System.out.println("(无故事)");
        } else {
            String storyTable = AsciiTable.getTable(monster.getStories(), Arrays.asList(
                    new Column().header("标题").with(Story::getTitle),
                    new Column().header("类型").with(s -> s.getStoryType().toString()),
                    new Column().header("评分").with(s -> String.valueOf(s.getStars())),
                    new Column().header("创建时间").with(s -> s.getCreateDate()),
                    new Column().header("内容").with(Story::getContent)

            ));
            System.out.println(storyTable);
        }

        // 添加故事或返回主菜单
        System.out.print("输入1添加故事，输入0返回主菜单: ");
        int action = scanner.nextInt();
        if (action == 1) {
            showAddStory(monster);
            // 重新显示详情
            showMonsterDetail(monster);
        } else {
            showStatistics();
        }

    }

    private static void showAddStory(Monster monster) {
        scanner.nextLine(); // 清除换行符
        System.out.print("请输入故事标题: ");
        String title = scanner.nextLine();
        System.out.print("请选择故事类型 (1: 搞笑类, 2: 冒险类, 3: 学习类): ");
        int typeChoice = scanner.nextInt();
        StoryType storyType;
        switch (typeChoice) {
            case 1 -> storyType = StoryType.FUNNY;
            case 2 -> storyType = StoryType.ADVENTURE;
            case 3 -> storyType = StoryType.LEARNING;
            default -> {
                System.out.println("无效的类型选择，默认设置为搞笑类");
                storyType = StoryType.FUNNY;
            }
        }
        System.out.print("请输入故事内容: ");
        scanner.nextLine();
        String content = scanner.nextLine();


        Story newStory = new Story(title, content, storyType);
        monster.addStory(newStory);
        System.out.println("故事添加成功！");
    }

    private static void initTestData() {
        for (int i = 1; i <= 3; i++) {

            Monster testMonster = new Monster("Test Monster " + i, 5 + i * i);
            testMonster.addStory(new Story("Story 1", "1111111111111111111", StoryType.FUNNY));
            testMonster.addStory(new Story("Story 2", "222222222222222222222", StoryType.FUNNY));
            monsters.add(testMonster);

            // 输出测试数据
            System.out.println("生成测试数据: " + testMonster);
            System.out.println("包含故事: ");
            for (Story story : testMonster.getStories()) {
                System.out.println("" + story);
            }
        }
    }

}
