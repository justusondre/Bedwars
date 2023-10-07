package generators;

public class GeneratorType {
    private final String name;
    private final int spawnInterval;

    public GeneratorType(String name, int spawnInterval) {
        this.name = name;
        this.spawnInterval = spawnInterval;
    }

    public String getName() {
        return name;
    }

    public int getSpawnInterval() {
        return spawnInterval;
    }

    @Override
    public String toString() {
        return name;
    }
}