package kr.dev.rei.counter4eun;

public class CountingData {
	private int idNum;
	private String name;
	private String description;
	private String descriptionTime;
	private int count;
	private String countTime;
	private int sortNum;

	public CountingData()
	{
	}

	public CountingData(int idNum, String name, String description,
			String descriptionTime, int count, String countTime,
			int sortNum) {
		super();
		this.idNum = idNum;
		this.name = name;
		this.description = description;
		this.descriptionTime = descriptionTime;
		this.count = count;
		this.countTime = countTime;
		this.sortNum = sortNum;
	}

	public int getIdNum() {
		return idNum;
	}
	public void setIdNum(int idNum) {
		this.idNum = idNum;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescriptionTime() {
		return descriptionTime;
	}
	public void setDescriptionTime(String descriptionTime) {
		this.descriptionTime = descriptionTime;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getCountTime() {
		return countTime;
	}
	public void setCountTime(String countTime) {
		this.countTime = countTime;
	}
	public int getSortNum() {
		return sortNum;
	}
	public void setSortNum(int sortNum) {
		this.sortNum = sortNum;
	}
}
