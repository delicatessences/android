package fr.delicatessences.delicatessences.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "vegetal_properties")
public class VegetalProperty{

	public static final String NAME_FIELD_NAME = "name";
	public static final String ID_FIELD_NAME = "_id";

    @DatabaseField(columnName = ID_FIELD_NAME, generatedId = true)
    private int mId;

	@DatabaseField(columnName = NAME_FIELD_NAME, unique = true)
	private String mName;
	
	public VegetalProperty() {
		super();
	}

	public VegetalProperty(String name) {
		super();
		this.mName = name;
	}


    public int getId(){return mId;}
	public String getName() {
		return mName;
	}
	
	
	
}
