package querymaker;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class SqlQueryCreator {

	// vytvori sa a vrati cast query, kde sa JOINuju tabulky
	public String createJoinQueryPart(MySQLQueryParameters mySqlParams) {

		String query = "";

		Set<String> pouziteTabulky = new TreeSet<>();

		for (int i = 1; i < mySqlParams.getNodeList().size(); i++) {

			String keywords;
			String sourceKey;
			String targetKey;
			String[] keywordArray;

			System.out.println(mySqlParams.getNodeList().get(i) + ":"
					+ mySqlParams.getNodeList().get(i - 1));
			keywords = mySqlParams.getVazbyDBKluce().get(
					mySqlParams.getNodeList().get(i)

					+ ":" + mySqlParams.getNodeList().get(i - 1));

			if (keywords == null) {
				keywords = mySqlParams.getVazbyDBKluce().get(
						mySqlParams.getNodeList().get(i - 1) + ":"
								+ mySqlParams.getNodeList().get(i));
				keywordArray = keywords.split(":");
				sourceKey = keywordArray[1];
				targetKey = keywordArray[0];

			} else {
				keywordArray = keywords.split(":");
				sourceKey = keywordArray[0];
				targetKey = keywordArray[1];

			}

			if (pouziteTabulky.contains(mySqlParams.getNodeList().get(i)) == true)
				continue;

			pouziteTabulky.add(mySqlParams.getNodeList().get(i));

			query += " JOIN "
					+ "`"
					+ mySqlParams.getNodeList().get(i)
					+ "` "
					+ mySqlParams.getSkratkyPouzitychTabuliek().get(
							mySqlParams.getNodeList().get(i))
					+ " "
					+ "ON "
					+ mySqlParams.getSkratkyPouzitychTabuliek().get(
							mySqlParams.getNodeList().get(i))
					+ "."
					+ sourceKey
					+ " = "
					+ mySqlParams.getSkratkyPouzitychTabuliek().get(
							mySqlParams.getNodeList().get(i - 1)) + "."
					+ targetKey + " \n";

		}

		return query;

	}

	// vytvorenie select casti
	public String createSelectQueryPart(MySQLQueryParameters mySqlParams) {

		String query = "";
		String argumenty = "";
		int argumentyCount = 0;

		Set<String> pouziteTabulky = new TreeSet<>();
		Set<String> pouziteStlpce;

		if (mySqlParams.getAgregAtribute() == null) {

			for (String uzol : mySqlParams.getNodeList()) {

				if (pouziteTabulky.contains(uzol) == true)
					continue;

				pouziteTabulky.add(uzol);

				System.out.println(uzol);
				System.out
						.println(mySqlParams.getDbTabulky().get(uzol).listOfDefaultColumns);

				pouziteStlpce = new TreeSet<>();

				for (String defaultColumn : mySqlParams.getDbTabulky()
						.get(uzol).listOfDefaultColumns) {

					System.out.println(defaultColumn);

					if (argumentyCount > 0)
						argumenty = argumenty + ", ";
					argumenty = argumenty
							+ mySqlParams.getSkratkyPouzitychTabuliek().get(
									uzol) + "." + defaultColumn;

					pouziteStlpce.add(defaultColumn);

					argumentyCount++;
				}

				for (UsedAtribute usedAtr : mySqlParams.getUsedAtributes()) {
					if (usedAtr.getEntityName().equals(uzol) == true
							&& pouziteStlpce
									.contains(usedAtr.getAtributeName()) == false) {
						if (argumentyCount > 0)
							argumenty = argumenty + ", ";
						argumenty = argumenty
								+ mySqlParams.getSkratkyPouzitychTabuliek()
										.get(uzol) + "."
								+ usedAtr.getAtributeName();

						pouziteStlpce.add(usedAtr.getAtributeName());

						argumentyCount++;

					}
				}
			}

			query = " SELECT "
					+ argumenty
					+ " FROM "
					+ "`"
					+ mySqlParams.getNodeList().get(0)
					+ "` "
					+ mySqlParams.getSkratkyPouzitychTabuliek().get(
							mySqlParams.getNodeList().get(0)) + " \n";

		} else {

			// vytvorenie selectu s agregacnou funkciou

			// klasicky agregacna funkcia

			// ak chyba v agragacnej funkcii entitia
			if (mySqlParams.getAgregAtribute().getEntity() == null) {

				argumenty = mySqlParams.getAgregAtribute().getFunction()
						+ "("
						+ mySqlParams.getAgregAtribute()
								.getOptionalFunctionParameter() + " ) AS "
						+ mySqlParams.getAgregAtribute().getAlias();

			} else

				argumenty = mySqlParams.getAgregAtribute().getFunction()
						+ "("
						+ mySqlParams.getAgregAtribute()
								.getOptionalFunctionParameter()
						+ " "
						+ mySqlParams.getSkratkyPouzitychTabuliek().get(
								mySqlParams.getAgregAtribute().getEntity())
						+ "."
						+ mySqlParams.getAgregAtribute().getEntityAtribute()
						+ ") as " + mySqlParams.getAgregAtribute().getAlias();

			if (mySqlParams.getGroupAtribute() == null) {

				query = " SELECT "
						+ argumenty
						+ " FROM "
						+ "`"
						+ mySqlParams.getNodeList().get(0)
						+ "` "
						+ mySqlParams.getSkratkyPouzitychTabuliek().get(
								mySqlParams.getNodeList().get(0)) + " \n";
			} else {

				// agregacna funkcia, kde vysledky dostavam na zaklade
				// zgrupovania

				argumentyCount = 1;

				for (String defaultColumn : mySqlParams.getDbTabulky().get(
						mySqlParams.getGroupAtribute().getEntity()).listOfDefaultColumns) {

					System.out.println(defaultColumn);

					if (argumentyCount > 0)
						argumenty = argumenty + ", ";
					argumenty = argumenty
							+ mySqlParams.getSkratkyPouzitychTabuliek().get(
									mySqlParams.getGroupAtribute().getEntity())
							+ "." + defaultColumn;
					argumentyCount++;
				}

				query = " SELECT "
						+ argumenty
						+ " FROM "
						+ "`"
						+ mySqlParams.getNodeList().get(0)
						+ "` "
						+ mySqlParams.getSkratkyPouzitychTabuliek().get(
								mySqlParams.getNodeList().get(0)) + " \n";

			}

		}

		return query;

	}

	// vytvorenie where casti
	public String createWhereQueryPart(MySQLQueryParameters mySqlParams) {

		String query = " WHERE ";

		TreeSet<String> setOfUsedAtributes;
		TreeSet<String> setOfMultipleUsedAtributes;
		ArrayList<WhereAtribute> listOfMultipleUsedAtributes = new ArrayList<>();
		setOfMultipleUsedAtributes = new TreeSet<>();
		setOfUsedAtributes = new TreeSet<>();

		// hladam atributy, ktore sa opakuju - vhodne spojit pomocou OR
		for (WhereAtribute atribute : mySqlParams.getWhereAtributes()) {
			if (setOfUsedAtributes.contains(atribute.getTableName()
					+ atribute.getAtributeName() + atribute.getOperator()) == true) {

				if (setOfMultipleUsedAtributes.contains(atribute.getTableName()
						+ atribute.getAtributeName() + atribute.getOperator()) == false) {

					setOfMultipleUsedAtributes.add(atribute.getTableName()
							+ atribute.getAtributeName()
							+ atribute.getOperator());

					listOfMultipleUsedAtributes.add(new WhereAtribute(atribute
							.getTableName(), atribute.getAtributeName(),
							atribute.getOperator(), null));
				}

			} else {

				setOfUsedAtributes.add(atribute.getTableName()
						+ atribute.getAtributeName() + atribute.getOperator());

			}
		}

		setOfMultipleUsedAtributes = new TreeSet<>();

		String actualQueryString;

		// spajam opakovane atributy pomocou OR
		for (WhereAtribute viacNasPouzAtribute : listOfMultipleUsedAtributes) {

			actualQueryString = "";

			setOfMultipleUsedAtributes.add(viacNasPouzAtribute.getTableName()
					+ viacNasPouzAtribute.getAtributeName()
					+ viacNasPouzAtribute.getOperator());

			for (WhereAtribute atribute : mySqlParams.getWhereAtributes()) {
				if (viacNasPouzAtribute.getTableName().equals(
						atribute.getTableName()) == true
						&& viacNasPouzAtribute.getAtributeName().equals(
								atribute.getAtributeName()) == true
						&& viacNasPouzAtribute.getOperator().equals(
								atribute.getOperator()) == true) {

					if ("".equals(actualQueryString) == false)
						actualQueryString += " OR ";
					else
						actualQueryString += " ( ";

					/*
					 * += mySqlParams.skratkyPouzitychTabuliek
					 * .get(atribute.tableName) + "." + atribute.atributeName +
					 * " " + atribute.operator + " '" +
					 * atribute.atributeParameter + "' ";
					 */

					if (atribute.getAtributeParameter() == "null")
						actualQueryString += mySqlParams
								.getSkratkyPouzitychTabuliek().get(
										atribute.getTableName())
								+ "."
								+ atribute.getAtributeName()
								+ " "
								+ atribute.getOperator()
								+ " "
								+ atribute.getAtributeParameter() + " ";
					else if (atribute.getAtributeName().startsWith("year")) {
						actualQueryString += atribute
								.getAtributeName()
								.replaceFirst(
										"year\\(",
										"year("
												+ mySqlParams
														.getSkratkyPouzitychTabuliek()
														.get(atribute
																.getTableName())
												+ ".")
								+ " "
								+ atribute.getOperator()
								+ " "
								+ atribute.getAtributeParameter() + " ";
					} else if (atribute.getAtributeName().startsWith("month")) {
						actualQueryString += atribute
								.getAtributeName()
								.replaceFirst(
										"month\\(",
										"month("
												+ mySqlParams
														.getSkratkyPouzitychTabuliek()
														.get(atribute
																.getTableName())
												+ ".")
								+ " "
								+ atribute.getOperator()
								+ " "
								+ atribute.getAtributeParameter() + " ";
					}

					else
						actualQueryString += mySqlParams
								.getSkratkyPouzitychTabuliek().get(
										atribute.getTableName())
								+ "."
								+ atribute.getAtributeName()
								+ " "
								+ atribute.getOperator()
								+ " '"
								+ atribute.getAtributeParameter() + "' ";

				}
			}

			actualQueryString += " ) ";

			if (query != " WHERE ")
				query += " AND ";

			query += actualQueryString;

		}

		// hladam ostatne atributy, ktore sa tu nachadzaju iba raz
		for (WhereAtribute atribute : mySqlParams.getWhereAtributes()) {

			if (setOfMultipleUsedAtributes.contains(atribute.getTableName()
					+ atribute.getAtributeName() + atribute.getOperator()) == true)
				continue;

			if (query != " WHERE ")
				query += " AND ";

			if (atribute.getAtributeParameter() == "null")
				query += mySqlParams.getSkratkyPouzitychTabuliek().get(
						atribute.getTableName())
						+ "."
						+ atribute.getAtributeName()
						+ " "
						+ atribute.getOperator()
						+ " "
						+ atribute.getAtributeParameter() + " ";
			else if (atribute.getAtributeName().startsWith("year")) {
				query += atribute.getAtributeName().replaceFirst(
						"year\\(",
						"year("
								+ mySqlParams.getSkratkyPouzitychTabuliek()
										.get(atribute.getTableName()) + ".")
						+ " "
						+ atribute.getOperator()
						+ " "
						+ atribute.getAtributeParameter() + " ";
			} else if (atribute.getAtributeName().startsWith("month")) {
				query += atribute.getAtributeName().replaceFirst(
						"month\\(",
						"month("
								+ mySqlParams.getSkratkyPouzitychTabuliek()
										.get(atribute.getTableName()) + ".")
						+ " "
						+ atribute.getOperator()
						+ " "
						+ atribute.getAtributeParameter() + " ";
			}

			else
				query += mySqlParams.getSkratkyPouzitychTabuliek().get(
						atribute.getTableName())
						+ "."
						+ atribute.getAtributeName()
						+ " "
						+ atribute.getOperator()
						+ " '"
						+ atribute.getAtributeParameter() + "' ";

		}

		if (query == " WHERE ")
			return "";
		else
			return query + "\n";
	}

	// vytvorenie group by casti
	public String createGroupQueryPart(MySQLQueryParameters mySqlParams) {
		String query = "";

		if (mySqlParams.getGroupAtribute() == null)
			return "";

		query = " GROUP BY("
				+ mySqlParams.getGroupAtribute().getOptionalFunctionParameter()
				+ " "
				+ mySqlParams.getSkratkyPouzitychTabuliek().get(
						mySqlParams.getGroupAtribute().getEntity()) + "."
				+ mySqlParams.getGroupAtribute().getEntityAtribute() + ")\n";

		return query;
	}

	public String createOrderQueryPart(MySQLQueryParameters mySqlParams) {
		String query = "";

		if (mySqlParams.getOrderAtribute() == null)
			return "";

		if (mySqlParams.getOrderAtribute().getEntity() == null)
			query = " ORDER BY " + mySqlParams.getOrderAtribute().getAtribute()
					+ " " + mySqlParams.getOrderAtribute().getSortDirection()
					+ " \n";
		else
			query = " ORDER BY "
					+ mySqlParams.getSkratkyPouzitychTabuliek().get(
							mySqlParams.getOrderAtribute().getEntity()) + "."
					+ mySqlParams.getOrderAtribute().getAtribute() + " "
					+ mySqlParams.getOrderAtribute().getSortDirection() + " \n";

		return query;
	}

	public String createLimitQueryPart(MySQLQueryParameters mySqlQueryParams) {

		String query = "";

		if (mySqlQueryParams.getLimitAtribute() != null) {
			query = " LIMIT " + mySqlQueryParams.getLimitAtribute().getCount()
					+ " \n";
		}

		return query;
	}

	public String createHavingQueryPart(MySQLQueryParameters mySqlQueryParams) {

		String query = "";

		if (mySqlQueryParams.getHavingAtribute() != null) {
			query = " HAVING "
					+ mySqlQueryParams.getHavingAtribute().getAtributeName()
					+ " " + mySqlQueryParams.getHavingAtribute().getOperator()
					+ " "
					+ mySqlQueryParams.getHavingAtribute().getAtributeValue()
					+ " \n";
		}

		return query;
	}

}
