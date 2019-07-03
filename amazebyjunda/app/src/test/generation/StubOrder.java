package generation;

public class StubOrder implements Order{
	private Builder builder;
	private MazeConfiguration mazeConfig;
	private int level;
	private boolean perfect;
	
	public StubOrder(Builder builder, int level, boolean perfect) {
		this.builder = builder;
		this.level = level;
		this.mazeConfig = new MazeContainer();
	}
	
	public Builder getBuilder() {
		return builder;
	}
	
	public int getSkillLevel() {
		return level;
	}
	
	public boolean isPerfect() {
		return perfect;
	}
	
	public void deliver(MazeConfiguration mazeConfig) {
		this.mazeConfig = mazeConfig;
	}
	
	public MazeConfiguration getConfiguration() {
		return mazeConfig;
	}

	@Override
	public void updateProgress(int percentage) {
	}
}
