package kr.co.greenart.product;

public enum ProductType {
	OUTER("outer"), 
	TOP("top"),
	BOTTOM("bottom"), 
	SHOES("shoes");
	
	private final String label;

	private ProductType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
	
	
}
