package kernbeisser.DBEntities.TypeFields;

import kernbeisser.DBEntities.*;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


//use to generate FieldIdentifier for all DBEntities
public class FieldUtils {
	@SneakyThrows
	public static void printClassToFile(File writeToDir, Class<?>... classes) {
		for (Class<?> clazz : classes) {
			FileWriter fw = new FileWriter(new File(writeToDir, clazz.getSimpleName() + "Field.java"));
			String header =
					"""
						package kernbeisser.DBEntities.TypeFields;
						
						import kernbeisser.DBConnection.FieldIdentifier;
						
						""";
			fw.write(header);
			fw.write("public class " + clazz.getSimpleName() + "Field {\n");
			for (Field declaredField : clazz.getDeclaredFields()) {
				if (Modifier.isStatic(declaredField.getModifiers())) {
					continue;
				}
				String statement =
						"public static FieldIdentifier<"
								+ clazz.getName()
								+ ","
								+ getTypeName(declaredField.getType())
								+ "> "
								+ declaredField.getName()
								+ " = new FieldIdentifier<>("
								+ clazz.getName()
								+ ".class, "
								+ getTypeName(declaredField.getType())
								+ ".class, \""
								+ declaredField.getName()
								+ "\");\n";
				fw.write(statement);
			}
			fw.write("\n}");
			fw.flush();
			fw.close();
		}
	}
	public static String getTypeName(Class<?> clazz) {
		if (!clazz.isPrimitive()) return clazz.getName();
		return switch (clazz.getSimpleName()) {
			case "double":
				yield "Double";
			case "int":
				yield "Integer";
			case "long":
				yield "Long";
			case "byte":
				yield "Byte";
			case "char":
				yield "Character";
			case "short":
				yield "Short";
			case "boolean":
				yield "Boolean";
			case "float":
				yield "Float";
			default:
				throw new UnsupportedOperationException("Type not supported");
		};
	}
	
	public static void main(String[] args) {
		printClassToFile(
				new File("app/src/main/java/kernbeisser/DBEntities/TypeFields"),
				Article.class,
				ArticlePrintPool.class,
				ArticleStock.class,
				CatalogEntry.class,
				IgnoredDialog.class,
				IgnoredDifference.class,
				Job.class,
				Offer.class,
				Permission.class,
				Post.class,
				PreOrder.class,
				PriceList.class,
				Purchase.class,
				SaleSession.class,
				SettingValue.class,
				Shelf.class,
				ShoppingItem.class,
				Supplier.class,
				SurchargeGroup.class,
				SystemSetting.class,
				Transaction.class,
				User.class,
				UserGroup.class,
				UserSettingValue.class
		);
	}
}


