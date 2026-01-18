package by.it.group410971.teterich.lesson14;

import java.util.*;

public class SitesB {
    static class DSU {
        private final Map<String, String> parent;
        private final Map<String, Integer> rank;
        private final Map<String, Integer> size;

        public DSU() {
            parent = new HashMap<>();
            rank = new HashMap<>();
            size = new HashMap<>();
        }

        public void makeSet(String site) {
            if (!parent.containsKey(site)) {
                parent.put(site, site);
                rank.put(site, 0);
                size.put(site, 1);
            }
        }

        public String find(String site) {
            if (!parent.get(site).equals(site)) {
                // Сжатие пути
                parent.put(site, find(parent.get(site)));
            }
            return parent.get(site);
        }

        public void union(String site1, String site2) {
            String root1 = find(site1);
            String root2 = find(site2);

            if (!root1.equals(root2)) {
                // Объединение по рангу
                if (rank.get(root1) < rank.get(root2)) {
                    parent.put(root1, root2);
                    size.put(root2, size.get(root2) + size.get(root1));
                } else if (rank.get(root1) > rank.get(root2)) {
                    parent.put(root2, root1);
                    size.put(root1, size.get(root1) + size.get(root2));
                } else {
                    parent.put(root2, root1);
                    rank.put(root1, rank.get(root1) + 1);
                    size.put(root1, size.get(root1) + size.get(root2));
                }
            }
        }

        public int getSize(String site) {
            return size.get(find(site));
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DSU dsu = new DSU();

        while (true) {
            String line = scanner.nextLine().trim();
            if (line.equals("end")) {
                break;
            }

            String[] sites = line.split("\\+");
            if (sites.length != 2) {
                continue;
            }

            String site1 = sites[0].trim();
            String site2 = sites[1].trim();

            // Создаем множества для сайтов, если их еще нет
            dsu.makeSet(site1);
            dsu.makeSet(site2);

            // Объединяем сайты
            dsu.union(site1, site2);
        }

        // Собираем размеры кластеров
        Map<String, Integer> clusterSizes = new HashMap<>();
        Set<String> allSites = new HashSet<>(dsu.parent.keySet());

        for (String site : allSites) {
            String root = dsu.find(site);
            clusterSizes.put(root, dsu.getSize(root));
        }

        // Получаем уникальные размеры кластеров
        List<Integer> sizes = new ArrayList<>(clusterSizes.values());

        // Сортируем по УБЫВАНИЮ
        Collections.sort(sizes, Collections.reverseOrder());

        // Выводим результат
        for (int i = 0; i < sizes.size(); i++) {
            System.out.print(sizes.get(i));
            if (i < sizes.size() - 1) {
                System.out.print(" ");
            }
        }
    }
}