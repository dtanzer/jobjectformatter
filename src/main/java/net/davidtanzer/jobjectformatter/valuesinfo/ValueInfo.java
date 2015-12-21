package net.davidtanzer.jobjectformatter.valuesinfo;

public class ValueInfo {
	private final String propertyName;
	private final String value;
	private final Class<?> fieldClass;

	public ValueInfo(final String propertyName, final String value, final Class<?> fieldClass) {
		this.propertyName = propertyName;
		this.value = value;
		this.fieldClass = fieldClass;
	}

	public String propertyName() {
		return propertyName;
	}

	public String value() {
		return value;
	}

	public Class<?> getFieldClass() {
		return fieldClass;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final ValueInfo valueInfo = (ValueInfo) o;

		if (propertyName != null ? !propertyName.equals(valueInfo.propertyName) : valueInfo.propertyName != null)
			return false;
		return value != null ? value.equals(valueInfo.value) : valueInfo.value == null;

	}

	@Override
	public int hashCode() {
		int result = propertyName != null ? propertyName.hashCode() : 0;
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "ValueInfo{" +
				"propertyName='" + propertyName + '\'' +
				", value='" + value + '\'' +
				'}';
	}
}
