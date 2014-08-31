package querymaker;

import graph.DijkstraPathFinder;
import graph.Vertex;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import parser.Word;
import xmlLoader.Column;
import xmlLoader.Neighbour;
import xmlLoader.Table;
import xmlLoader.TableLoader;
import database.DatabaseManager;

public class QueryCreator {

	private List<WhereAtribute> whereAtributes;
	private List<String> listOfUsedEntities;
	private Set<String> usedEntities;

	private List<UsedAtribute> listOfUsedAtributes;
	private HashMap<String, UsedAtribute> usedAtributes;

	private AgregateAtribute agregAtribute = null;
	private GroupAtribute groupAtribute = null;
	private OrderAtribute orderAtribute = null;
	private LimitAtribute limitAtribute = null;
	private HavingAtribute havingAtribute = null;

	// vytvori a vrati zoznam tabuliek, cez ktore vedie query
	private List<String> getNodeList(List<String> zoznamZnamychVstupov,
			List<Table> tableList) {

		List<String> nodeList = new ArrayList<>();

		if (zoznamZnamychVstupov.size() > 1) {
			// hladanie cesty medzi tabulkami
			DijkstraPathFinder dijkstra = new DijkstraPathFinder();
			List<Vertex> vyslednaCesta = dijkstra.DijkstraAlgorithm(tableList,
					zoznamZnamychVstupov);
			for (Vertex v : vyslednaCesta) {
				nodeList.add(v.getName());
			}
		} else if (zoznamZnamychVstupov.size() == 1) {
			// zoznam vstupov == 1
			nodeList.add(zoznamZnamychVstupov.get(0));
		}

		return nodeList;
	}

	// vytvoria sa aliasy pre tabulky v dopyte
	private HashMap<String, String> createTableNicknames(List<String> nodeList,
			HashMap<String, String> bondDbKeys) {

		// System.out.println("Hladanie spojovacich klucov...");
		HashMap<String, String> tableAliasOfUsedTables = new HashMap<String, String>();

		int pocetPouzitychSkratiekTabuliek = 0;

		if (nodeList.size() == 1) {

			tableAliasOfUsedTables
					.put(nodeList.get(0),
							Character.toString(nodeList.get(0).charAt(0))
									+ Integer
											.toString(pocetPouzitychSkratiekTabuliek++));

		} else {

			for (int i = 0; i < nodeList.size() - 1; i++) {

				String keys = bondDbKeys.get(nodeList.get(i) + ":"
						+ nodeList.get(i + 1));
				if (keys == null)
					keys = bondDbKeys.get(nodeList.get(i + 1) + ":"
							+ nodeList.get(i));

				if (tableAliasOfUsedTables.get(nodeList.get(i)) == null)
					tableAliasOfUsedTables
							.put(nodeList.get(i),
									Character.toString(nodeList.get(i)
											.charAt(0))
											+ Integer
													.toString(pocetPouzitychSkratiekTabuliek++));

				if (tableAliasOfUsedTables.get(nodeList.get(i + 1)) == null)
					tableAliasOfUsedTables
							.put(nodeList.get(i + 1),
									Character.toString(nodeList.get(i + 1)
											.charAt(0))
											+ Integer
													.toString(pocetPouzitychSkratiekTabuliek++));

			}
		}

		return tableAliasOfUsedTables;

	}

	private boolean hasTag(Word word) {

		if (word != null && word.getTag() != null
				&& word.getTag().isEmpty() == false)
			return true;
		else
			return false;
	}

	// nacitanie product types a vratenie hashtable s menom tabulky a argumentom
	private HashMap<String, String> loadProductTypes() {
		DatabaseManager dm = DatabaseManager.getInstance();
		ResultSet rs;

		HashMap<String, String> entityMap = new HashMap<String, String>();

		rs = dm.executeDbQuery("SELECT name FROM product_type");

		try {
			while (rs.next()) {
				String columnValue = rs.getString(1);
				entityMap.put(columnValue, "product_type:name");

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			return entityMap;
		}

		return entityMap;

	}

	private void addEntityToUsedEntities(Set<String> usedEntities,
			List<String> listOfUsedEntities, String entityName) {

		if (usedEntities.contains(entityName) == false) {
			listOfUsedEntities.add(entityName);
			usedEntities.add(entityName);
		}

	}

	public String createQuery(List<Word> wordList) {

		// hash mapa obsahujuca vazby a kluce
		HashMap<String, String> bondDbKeys = new HashMap<String, String>();
		// hash mapa pre indexovanie tabuliek databazy
		HashMap<String, Table> dbTables = new HashMap<String, Table>();
		// hash mapa pre atributy tabuliek
		HashMap<String, TableAtribute> atributy = new HashMap<String, TableAtribute>();

		// hash mapa s mesiacmi
		@SuppressWarnings("serial")
		HashMap<String, Integer> mesiace = new HashMap<String, Integer>() {
			{
				put("január", 1);
				put("február", 2);
				put("marec", 3);
				put("apríl", 4);
				put("máj", 5);
				put("jún", 6);
				put("júl", 7);
				put("august", 8);
				put("september", 9);
				put("október", 10);
				put("november", 11);
				put("december", 12);
			}
		};

		HashMap<String, String> productTypesHashMap = loadProductTypes();
		whereAtributes = new ArrayList<>();

		listOfUsedEntities = new ArrayList<>();
		usedEntities = new TreeSet<>();

		listOfUsedAtributes = new ArrayList<>();
		usedAtributes = new HashMap<>();

		// NACITANIE VSTUPNYCH TABULIEK PRE ATRIBUTY TABULIEK DATABAZY
		TableLoader tableLoader = TableLoader.getInstance();
		tableLoader.loadTables();
		List<Table> tableList = tableLoader.getTables();

		// INICIALIZOVANIE HASH MAP pre dbTables a bondDbKeys a atributy
		for (Table table : tableList) {
			dbTables.put(table.getName(), table);
			dbTables.put(table.getLabel(), table);
			for (Neighbour neighbour : table.getListOfNeighbours()) {
				bondDbKeys.put(
						table.getName() + ":" + neighbour.getTarget(),
						neighbour.getSourcekey() + ":"
								+ neighbour.getTargetkey());
			}
			table.columnMap = new HashMap<>();
			for (Column column : table.getListOfColumns()) {
				atributy.put(
						column.getLabel(),
						new TableAtribute(table.getName(), column
								.getColumnName(), column.getLabel(), column
								.getTag()));

				table.columnMap.put(column.getColumnName(), column);
			}
		}

		// prechadzanie kazdeho slova vo vete a jeho nasledna analyza
		for (int i = 0; i < wordList.size(); i++) {

			// hladam ci dane slovo nie je nejaky atribut - stlpec v nejakej
			// tabulke
			TableAtribute tableAtribute = atributy.get(wordList.get(i)
					.getLema());

			if (tableAtribute != null) {

				// dane slovo je atribut - stlpec nejakej tabulky

				if (wordList.get(i).isUsed() == false) {
					wordList.get(i).setUsed(true);

					if (usedAtributes.get(tableAtribute.getName()) == null) {
						usedAtributes.put(tableAtribute.getName(),
								new UsedAtribute(tableAtribute.getTable(),
										tableAtribute.getName()));
						listOfUsedAtributes.add(new UsedAtribute(tableAtribute
								.getTable(), tableAtribute.getName()));
					}

					// kontrolujem, ze uz som raz tuto tabulku nezahrnul do
					// identifikovanych entity
					addEntityToUsedEntities(usedEntities,
							getListOfUsedEntities(), tableAtribute.getTable());

				}

				// pozeram, ze aky parameter sa vztahuje na dany atribut
				String tagParameterOfAtribute = tableAtribute.getTag();

				// zistujem, ci to je pridavne meno - tag zacina na A
				if (tagParameterOfAtribute.isEmpty() == false
						&& tagParameterOfAtribute.charAt(0) == 'A') {

					// zistujem, ci je pridavne meno pred atributom - tj.
					// pridavne meno sa vztahuje na tento atribut
					if (hasTag(wordList.get(i - 1)) == true
							&& wordList.get(i - 1).getTag().charAt(0) == 'A') {

						if (wordList.get(i - 1).isUsed() == false) {

							wordList.get(i - 1).setUsed(true);

							// pridam pridavne meno s danym atributom do where
							// podmienky
							getWhereAtributes().add(
									new WhereAtribute(tableAtribute.getTable(),
											tableAtribute.getName(), "=",
											wordList.get(i - 1).getLema()));

						}
					}

					// zistujem, ci to je vlastne podstatne meno - tag zacina na
					// X
				} else if (tagParameterOfAtribute.isEmpty() == false
						&& tagParameterOfAtribute.charAt(0) == 'X') {

					// vlastne mena - po atribute

					// pozeram, ze ci za atributom nasleduje vlastne podstatne
					// meno

					if ((i + 1) < wordList.size()
							&& hasTag(wordList.get(i + 1)) == true
							&& wordList.get(i + 1).getTag().charAt(0) == 'X') {

						// vlastne podstatne meno nema lemu, preto sa pouziva
						// source word, ake je vo vstupe

						if (wordList.get(i + 1).isUsed() == false) {

							wordList.get(i + 1).setUsed(true);

							getWhereAtributes()
									.add(new WhereAtribute(tableAtribute
											.getTable(), tableAtribute
											.getName(), "=", wordList
											.get(i + 1).getSourceWord()));
						}

					}
				}

			} else if (productTypesHashMap.get(wordList.get(i).getLema()) != null) {

				// dane slovo je parameter atributu pre typ produktu - napr.
				// telefon, monitor

				String entityAndColumn[] = productTypesHashMap.get(
						wordList.get(i).getLema()).split(":");

				if (wordList.get(i).isUsed() == false) {

					wordList.get(i).setUsed(true);

					getWhereAtributes().add(
							new WhereAtribute(entityAndColumn[0],
									entityAndColumn[1], "=", wordList.get(i)
											.getLema()));

					// kontrolujem, ze uz som raz tuto tabulku nezahrnul do
					// identifikovanych entity
					addEntityToUsedEntities(usedEntities,
							getListOfUsedEntities(), entityAndColumn[0]);
					addEntityToUsedEntities(usedEntities,
							getListOfUsedEntities(), "product");

				}

			} else if (i - 1 >= 0
					&& hasTag(wordList.get(i)) == true
					&& wordList.get(i).getTag().charAt(0) == 'X'
					&& productTypesHashMap.get(wordList.get(i - 1).getLema()) != null) {

				// vlastne podstatne meno sa vstahuje na typ produktu, takze
				// pridam vlastne meno ako parameter atributu - meno produktu

				if (wordList.get(i).isUsed() == false) {

					wordList.get(i).setUsed(true);

					getWhereAtributes().add(
							new WhereAtribute("product", "name", "=", wordList
									.get(i).getSourceWord()));

					// kontrolujem, ze uz som raz tuto tabulku nezahrnul do
					// identifikovanych entity
					addEntityToUsedEntities(usedEntities,
							getListOfUsedEntities(), "product");
				}

			} else {

				// pozeram, ci dane slovo predstavuje entitu

				Table searchedEntityTable = dbTables.get(wordList.get(i)
						.getLema());

				if (searchedEntityTable != null) {

					// ak som urcil, ze slovo predstavuje entitu, tak ho pridam

					if (wordList.get(i).isUsed() == false) {

						wordList.get(i).setUsed(true);

						addEntityToUsedEntities(usedEntities,
								getListOfUsedEntities(),
								searchedEntityTable.getName());
					}

				} else {
					// tu pozeram podstatne mena + pridavne a ine...

					// pozeram, ci dane slovo je vlastne meno
					if (hasTag(wordList.get(i)) == true
							&& wordList.get(i).getTag().charAt(0) == 'X') {

						// dane slovo je vlastne meno, hladam k nemu
						// zodpovedajucu entitu

						searchedEntityTable = dbTables.get(wordList.get(i - 1)
								.getLema());
						if (searchedEntityTable != null) {

							// pred danym slovom sa skutocne nachadza entita,
							// teraz prehladama default stlpec danej tabulky, ci
							// nenajdem atribut, ktory vyzaduje vlastne meno

							for (int j = 0; j < searchedEntityTable.listOfDefaultColumns
									.size(); j++) {

								// prechadzam nasledujuce slova, a hladam, ci sa
								// zhoduju s defaultnymi parametrami atributov
								// danej entity - tj. pre kazdy default column
								// pozeram, ci argument wordlist(i) moze byt
								// pren vhodny + musim kuknut aj dalej, ci tie
								// vlastne podstatne mena X nepokracuju dalej
								if (i + j < wordList.size()
										&& hasTag(wordList.get(i + j)) == true
										&& wordList.get(i + j).getTag()
												.charAt(0) == searchedEntityTable.columnMap
												.get(searchedEntityTable.listOfDefaultColumns
														.get(j)).getTag()
												.charAt(0)) {

									if (wordList.get(i + j).isUsed() == false) {

										wordList.get(i + j).setUsed(true);

										getWhereAtributes()
												.add(new WhereAtribute(
														searchedEntityTable
																.getName(),
														searchedEntityTable.listOfDefaultColumns
																.get(j),
														"=",
														wordList.get(i + j)
																.getSourceWord()));

										addEntityToUsedEntities(usedEntities,
												getListOfUsedEntities(),
												searchedEntityTable.getName());
									}

								}

							}
						}
					}

				}

			}

			// datum kontrolujem specialne pre cenu
			if ("cena".equals(wordList.get(i).getLema())) {

				int j = i + 1;

				if (i - 1 >= 0
						&& wordList.get(i - 1).getLema() != null
						&& wordList.get(i - 1).getLema().startsWith("aktuáln") == true) {

					if (wordList.get(i - 1).isUsed() == false) {

						wordList.get(i - 1).setUsed(true);

						getWhereAtributes().add(
								new WhereAtribute("product_price", "date_to",
										"is", "null"));

					}
				}

				else

					while (j < wordList.size()
							&& "objednávka".equals(wordList.get(j).getLema()) == false) {

						// prechadzam slova nasledujuce za cenou a hladam, ci je
						// tam
						// uvedeny datum, na aky sa viaze

						if ("rok".equals(wordList.get(j).getLema())) {
							// ak som nasiel, ze na cenu sa vztahuje slovo rok,
							// tak
							// ho pridam ako atribut

							if (wordList.get(j).isUsed() == false)
								wordList.get(j).setUsed(true);

							// za rokom sa vzdy ma nachadzat cislo toho roku
							if (hasTag(wordList.get(j + 1)) == true
									&& wordList.get(j + 1).getTag().charAt(0) == 'n') {

								if (wordList.get(j + 1).isUsed() == false) {
									wordList.get(j + 1).setUsed(true);

									getWhereAtributes().add(
											new WhereAtribute("product_price",
													"year(date_from)", "<=",
													wordList.get(j + 1)
															.getLema()
															.toString()));

									getWhereAtributes().add(
											new WhereAtribute("product_price",
													"year(date_to)", ">=",
													wordList.get(j + 1)
															.getLema()
															.toString()));

									addEntityToUsedEntities(usedEntities,
											getListOfUsedEntities(),
											"product_price");
								}

								j++;
							}
						}

						else if ("mesiac".equals(wordList.get(j).getLema())) {

							// ak som nasiel, ze na cenu sa vztahuje slovo
							// mesiac,
							// tak ho pridam ako atribut

							if (wordList.get(j).isUsed() == false)
								wordList.get(j).setUsed(true);

							if (wordList.get(j + 1).isUsed() == false) {
								wordList.get(j + 1).setUsed(true);

								getWhereAtributes().add(
										new WhereAtribute("product_price",
												"month(date_from)", "<=",
												mesiace.get(
														wordList.get(j + 1)
																.getLema())
														.toString()));

								getWhereAtributes().add(
										new WhereAtribute("product_price",
												"month(date_to)", ">=", mesiace
														.get(wordList
																.get(j + 1)
																.getLema())
														.toString()));

								addEntityToUsedEntities(usedEntities,
										getListOfUsedEntities(),
										"product_price");
							}
							j++;
						}

						else if (mesiace.get(wordList.get(j).getLema()) != null) {

							// ak som nasiel vo vete spomenuty nejaky mesiac,
							// hladam
							// pred nim predlozku urcujucu viac tento datum, tag
							// predlozky je E

							if (j - 2 >= 0
									&& hasTag(wordList.get(j - 2)) == true
									&& wordList.get(j - 2).getTag().charAt(0) == 'E') {

								if ("od".equals(wordList.get(j - 2).getLema())) {
									// nasiel som predlozku od, urcujuca
									// zaciatok
									// datumu

									if (wordList.get(j - 2).isUsed() == false
											&& wordList.get(j - 1).isUsed() == false
											&& wordList.get(j).isUsed() == false
											&& wordList.get(j + 1).isUsed() == false) {

										wordList.get(j - 2).setUsed(true);
										wordList.get(j - 1).setUsed(true);
										wordList.get(j).setUsed(true);
										wordList.get(j + 1).setUsed(true);

										getWhereAtributes()
												.add(new WhereAtribute(
														"product_price",
														"date_to",
														">=",
														wordList.get(j + 1)
																.getLema()
																.toString()
																+ "-"
																+ mesiace
																		.get(wordList
																				.get(j)
																				.getLema())
																		.toString()
																+ "-"
																+ wordList
																		.get(j - 1)
																		.getLema()
																		.toString()));

										addEntityToUsedEntities(usedEntities,
												getListOfUsedEntities(),
												"product_price");
									}

								} else if ("do".equals(wordList.get(j - 2)
										.getLema())) {

									// nasiel som predlozku do, urcujuca koniec
									// datumu

									if (wordList.get(j - 2).isUsed() == false
											&& wordList.get(j - 1).isUsed() == false
											&& wordList.get(j).isUsed() == false
											&& wordList.get(j + 1).isUsed() == false) {

										wordList.get(j - 2).setUsed(true);
										wordList.get(j - 1).setUsed(true);
										wordList.get(j).setUsed(true);
										wordList.get(j + 1).setUsed(true);

										getWhereAtributes()
												.add(new WhereAtribute(
														"product_price",
														"date_from",
														"<=",
														wordList.get(j + 1)
																.getLema()
																.toString()
																+ "-"
																+ mesiace
																		.get(wordList
																				.get(j)
																				.getLema())
																		.toString()
																+ "-"
																+ wordList
																		.get(j - 1)
																		.getLema()
																		.toString()));

										addEntityToUsedEntities(usedEntities,
												getListOfUsedEntities(),
												"product_price");
									}
								}
							}
							j = j + 2;

						}
						j++;
					}
			}

			// datum kontrolujem specialne pre objednavku
			if ("objednávka".equals(wordList.get(i).getLema())) {

				/*
				 * 
				 * if ("minulý".equals(wordList.get(i).getLema()) == true) {
				 * //pozeram co je za slovom minuly - bud rok alebo mesiacF
				 * if("rok".equals(wordList.get(i).getLema()) == true) {
				 * whereAtributes.add(new WhereAtribute(tableName, atributeName,
				 * operator, atributeParameter))
				 * DATE(FROM_UNIXTIME(UNIX_TIMESTAMP() - 365*24*60*60))
				 */

				int j = i + 1;

				while (j < wordList.size()
						&& "cena".equals(wordList.get(j).getLema()) == false) {

					// prechadzam slova nasledujuce za cenou a hladam, ci je tam
					// uvedeny datum, na aky sa viaze

					if ("rok".equals(wordList.get(j).getLema())
							&& (j + 1) < wordList.size()) {

						// ak som nasiel, ze na objednavku sa vztahuje slovo
						// rok, tak ho pridam ako atribut

						// za rokom sa vzdy ma nachadzat cislo toho roku

						if (hasTag(wordList.get(j + 1)) == true
								&& wordList.get(j + 1).getTag().charAt(0) == 'n') {

							if (wordList.get(j).isUsed() == false
									&& wordList.get(j + 1).isUsed() == false) {

								wordList.get(j).setUsed(true);
								wordList.get(j + 1).setUsed(true);

								if (hasTag(wordList.get(j - 1)) == true
										&& wordList.get(j - 1).getTag()
												.charAt(0) == 'E') {

									wordList.get(j - 1).setUsed(true);

									if ("od".equals(wordList.get(j + 1)
											.getLema()) == true)
										getWhereAtributes().add(
												new WhereAtribute("order",
														"year(date)", "<=",
														wordList.get(j + 1)
																.getLema()
																.toString()));
									else
										getWhereAtributes().add(
												new WhereAtribute("order",
														"year(date)", ">=",
														wordList.get(j + 1)
																.getLema()
																.toString()));

								} else
									getWhereAtributes().add(
											new WhereAtribute("order",
													"year(date)", "=", wordList
															.get(j + 1)
															.getLema()
															.toString()));

								addEntityToUsedEntities(usedEntities,
										getListOfUsedEntities(), "order");
							}
							j++;
						}

					} else if ("mesiac".equals(wordList.get(j).getLema())
							&& (j + 1) < wordList.size()) {

						// ak som nasiel, ze na objednavku sa vztahuje slovo
						// mesiac, tak ho pridam ako atribut
						if (wordList.get(j).isUsed() == false
								&& wordList.get(j + 1).isUsed() == false) {

							wordList.get(j).setUsed(true);
							wordList.get(j + 1).setUsed(true);

							if (hasTag(wordList.get(j - 1)) == true
									&& wordList.get(j - 1).getTag().charAt(0) == 'E') {

								wordList.get(j - 1).setUsed(true);

								if ("od".equals(wordList.get(j - 1).getLema()) == true)
									getWhereAtributes().add(
											new WhereAtribute("order",
													"month(date)", "<=",
													mesiace.get(
															wordList.get(j + 1)
																	.getLema())
															.toString()));
								else if ("do".equals(wordList.get(j - 1)
										.getLema()) == true)
									getWhereAtributes().add(
											new WhereAtribute("order",
													"month(date)", ">=",
													mesiace.get(
															wordList.get(j + 1)
																	.getLema())
															.toString()));

								else if ("za".equals(wordList.get(j - 1)
										.getLema()) == true)
									getWhereAtributes().add(
											new WhereAtribute("order",
													"month(date)", "=", mesiace
															.get(wordList.get(
																	j + 1)
																	.getLema())
															.toString()));

							} else
								getWhereAtributes().add(
										new WhereAtribute("order",
												"month(date)", "=", mesiace
														.get(wordList
																.get(j + 1)
																.getLema())
														.toString()));

							addEntityToUsedEntities(usedEntities,
									getListOfUsedEntities(), "order");
						}
						j++;

					} else if (mesiace.get(wordList.get(j).getLema()) != null) {

						// ak som nasiel vo vete spomenuty nejaky mesiac, hladam
						// pred nim predlozku urcujucu viac tento datum, tag
						// predlozky je E

						if (hasTag(wordList.get(j - 2)) == true
								&& wordList.get(j - 2).getTag().charAt(0) == 'E') {

							if ("od".equals(wordList.get(j - 2).getLema())) {

								// nasiel som predlozku od, urcujuca zaciatok
								// datumu
								if (wordList.get(j - 2).isUsed() == false
										&& wordList.get(j - 1).isUsed() == false
										&& wordList.get(j).isUsed() == false
										&& wordList.get(j + 1).isUsed() == false) {

									wordList.get(j - 2).setUsed(true);
									wordList.get(j - 1).setUsed(true);
									wordList.get(j).setUsed(true);
									wordList.get(j + 1).setUsed(true);
									getWhereAtributes()
											.add(new WhereAtribute(
													"order",
													"date",
													">=",
													wordList.get(j + 1)
															.getLema()
															.toString()
															+ "-"
															+ mesiace
																	.get(wordList
																			.get(j)
																			.getLema())
																	.toString()
															+ "-"
															+ wordList
																	.get(j - 1)
																	.getLema()
																	.toString()));

									addEntityToUsedEntities(usedEntities,
											getListOfUsedEntities(), "order");
								}

							} else if ("do".equals(wordList.get(j - 2)
									.getLema())) {

								// nasiel som predlozku do, urcujuca koniec
								// datumu

								if (wordList.get(j - 2).isUsed() == false
										&& wordList.get(j - 1).isUsed() == false
										&& wordList.get(j).isUsed() == false
										&& wordList.get(j + 1).isUsed() == false) {

									wordList.get(j - 2).setUsed(true);
									wordList.get(j - 1).setUsed(true);
									wordList.get(j).setUsed(true);
									wordList.get(j + 1).setUsed(true);

									getWhereAtributes()
											.add(new WhereAtribute(
													"order",
													"date",
													"<=",
													wordList.get(j + 1)
															.getLema()
															.toString()
															+ "-"
															+ mesiace
																	.get(wordList
																			.get(j)
																			.getLema())
																	.toString()
															+ "-"
															+ wordList
																	.get(j - 1)
																	.getLema()
																	.toString()));

									addEntityToUsedEntities(usedEntities,
											getListOfUsedEntities(), "order");
								}
							}
						}

						j = j + 2;

					}
					j++;
				}
			}
		}

		// HLADADANIE AGREGACII A KLUCOVYCH SLOV
		for (int i = 0; i < wordList.size(); i++) {

			if (hasTag(wordList.get(i)) == true
					&& wordList.get(i).getTag().charAt(0) == 'P') {
				// zamena

				// zameno kolko + naviazanie dalsieho slova priemerne
				if ("ko¾ko".equals(wordList.get(i).getLema()) == true) {

					wordList.get(i).setUsed(true);

					int j = i + 1;

					while (j < wordList.size()) {

						TableAtribute tableAtribute = atributy.get(wordList
								.get(j).getLema());

						if (dbTables.get(wordList.get(j).getLema()) != null) {

							if ("cena".equals(wordList.get(j).getLema()) == true) {

								addEntityToUsedEntities(usedEntities,
										getListOfUsedEntities(),
										"product_price");
								wordList.get(i).setUsed(true);
								break;

							} else if ("platba".equals(wordList.get(j)
									.getLema()) == true) {

								agregAtribute = new AgregateAtribute("SUM", "",
										dbTables.get(wordList.get(j).getLema())
												.getName(), "amount", "`suma`");
								setGroupAtribute(new GroupAtribute("", "order",
										"id_order"));

							} else if (tableAtribute != null) {
								agregAtribute = new AgregateAtribute("count",
										"", tableAtribute.getTable(), "id_"
												+ tableAtribute.getTable(),
										"`poèet`");
								break;
							}

							else if (productTypesHashMap.get(wordList.get(j)
									.getLema()) != null) {

								String tabulkaAStelpec[] = productTypesHashMap
										.get(wordList.get(j).getLema()).split(
												":");

								agregAtribute = new AgregateAtribute("count",
										"", tabulkaAStelpec[0], "id_"
												+ tabulkaAStelpec[0], "`poèet`");

								break;

							} else

								agregAtribute = new AgregateAtribute("COUNT",
										"", dbTables.get(
												wordList.get(j).getLema())
												.getName(), "id_"
												+ dbTables.get(
														wordList.get(j)
																.getLema())
														.getName(), "`poèet`");
							break;

						} else if ("priemerne".equals(wordList.get(i + 1)
								.getLema()) == true) {

							wordList.get(i + 1).setUsed(true);

							j = i + 1;

							while (j < wordList.size()) {

								tableAtribute = atributy.get(wordList.get(j)
										.getLema());

								if (tableAtribute != null) {
									agregAtribute = new AgregateAtribute("avg",
											"", tableAtribute.getTable(),
											tableAtribute.getName(),
											"`priemer`");

									break;
								}

								else if (productTypesHashMap.get(wordList
										.get(j).getLema()) != null) {

									String tabulkaAStelpec[] = productTypesHashMap
											.get(wordList.get(j).getLema())
											.split(":");

									agregAtribute = new AgregateAtribute("avg",
											"", tabulkaAStelpec[0], "id_"
													+ tabulkaAStelpec[0],
											"`priemer`");
									break;

								}

								j++;
							}

							break;

						}

						j++;
					}

				} else if ("kto".equals(wordList.get(i).getLema())) {

					addEntityToUsedEntities(usedEntities,
							getListOfUsedEntities(), "customer");

					wordList.get(i).setUsed(true);

				} else if ("kde".equals(wordList.get(i).getLema())) {

					wordList.get(i).setUsed(true);

					if (usedAtributes.get("adress") == null) {
						usedAtributes.put("adress", new UsedAtribute(
								"customer", "adress"));
						listOfUsedAtributes.add(new UsedAtribute("customer",
								"adress"));
					}

					if (usedAtributes.get("house_number") == null) {
						usedAtributes.put("house_number", new UsedAtribute(
								"customer", "house_number"));
						listOfUsedAtributes.add(new UsedAtribute("customer",
								"house_number"));
					}

					if (usedAtributes.get("city") == null) {
						usedAtributes.put("city", new UsedAtribute("customer",
								"city"));
						listOfUsedAtributes.add(new UsedAtribute("customer",
								"city"));
					}

					if (usedAtributes.get("country") == null) {
						usedAtributes.put("country", new UsedAtribute(
								"customer", "country"));
						listOfUsedAtributes.add(new UsedAtribute("customer",
								"country"));
					}

				} else if (wordList.get(i).getLema() != null
						&& wordList.get(i).getLema().startsWith("každ") == true) {

					wordList.get(i).setUsed(true);

					Table searchedEntityTable = dbTables.get(wordList
							.get(i + 1).getLema());

					if (searchedEntityTable != null) {

						wordList.get(i + 1).setUsed(true);

						setGroupAtribute(new GroupAtribute("",
								searchedEntityTable.getName(), "id_"
										+ searchedEntityTable.getName()));

						addEntityToUsedEntities(usedEntities,
								getListOfUsedEntities(),
								searchedEntityTable.getName());
					}
				}

			} else if (hasTag(wordList.get(i)) == true
					&& wordList.get(i).getTag().charAt(0) == 'A') {
				// pridavne mena

				if ("priemerná".equals(wordList.get(i).getLema()) == true) {

					wordList.get(i).setUsed(true);

					int j = i + 1;

					while (j < wordList.size()) {

						TableAtribute tableAtribute = atributy.get(wordList
								.get(j).getLema());

						if (tableAtribute != null) {
							agregAtribute = new AgregateAtribute("avg", "",
									tableAtribute.getTable(),
									tableAtribute.getName(), "`priemer`");

							break;
						}

						j++;
					}

				} else if (wordList.get(i).getLema() != null
						&& wordList.get(i).getLema().startsWith("rôzn") == true) {

					wordList.get(i).setUsed(true);

					if (agregAtribute != null)
						agregAtribute.setOptionalFunctionParameter("distinct");

				} else if (wordList.get(i).getSourceWord()
						.startsWith("najlacn") == true
						|| wordList.get(i).getSourceWord()
								.startsWith("najdrah") == true) {

					wordList.get(i).setUsed(true);

					addEntityToUsedEntities(usedEntities,
							getListOfUsedEntities(), "product_price");

					if (wordList.get(i).getSourceWord().startsWith("najdrah") == true)
						orderAtribute = new OrderAtribute("product_price",
								"price", "DESC");
					else
						orderAtribute = new OrderAtribute("product_price",
								"price", "ASC");

					// zistujem ci to ej jednotne alebo mnozne cislo + ci je
					// specifikovany pocet

					if (hasTag(wordList.get(i))
							&& wordList.get(i).getTag().charAt(3) == 's') {
						limitAtribute = new LimitAtribute("1");

					} else if (i - 1 >= 0 && hasTag(wordList.get(i - 1))
							&& wordList.get(i - 1).getTag().charAt(0) == 'n') {
						limitAtribute = new LimitAtribute(wordList.get(i - 1)
								.getSourceWord());

					}

				} else if (wordList.get(i).getSourceWord().startsWith("lacn") == true
						|| wordList.get(i).getSourceWord().startsWith("drah") == true) {

					wordList.get(i).setUsed(true);

					addEntityToUsedEntities(usedEntities,
							getListOfUsedEntities(), "product_price");

					if (wordList.get(i).getSourceWord().startsWith("lacn") == true)
						getWhereAtributes().add(
								new WhereAtribute("product_price", "price",
										">", wordList.get(i + 2)
												.getSourceWord()));
					else
						getWhereAtributes().add(
								new WhereAtribute("product_price", "price",
										">", wordList.get(i + 2)
												.getSourceWord()));

				}

			} else if (hasTag(wordList.get(i)) == true
					&& wordList.get(i).getTag().charAt(0) == 'S') {
				// podstatne mena

				if ("poèet".equals(wordList.get(i).getLema()) == true) {

					wordList.get(i).setUsed(true);

					int j = i + 1;

					while (j < wordList.size()) {

						TableAtribute tableAtribute = atributy.get(wordList
								.get(j).getLema());
						if (tableAtribute != null) {

							agregAtribute = new AgregateAtribute("count", "",
									tableAtribute.getTable(), "id_"
											+ tableAtribute.getTable(),
									"`poèet`");

							break;
						}

						else if (productTypesHashMap.get(wordList.get(j)
								.getLema()) != null) {

							String tabulkaAStelpec[] = productTypesHashMap.get(
									wordList.get(j).getLema()).split(":");

							agregAtribute = new AgregateAtribute("count", "",
									tabulkaAStelpec[0], "id_"
											+ tabulkaAStelpec[0], "`poèet`");

							break;

						} else if (dbTables.get(wordList.get(j).getLema()) != null) {

							agregAtribute = new AgregateAtribute("count", "",
									dbTables.get(wordList.get(j).getLema())
											.getName(), "id_"
											+ dbTables.get(
													wordList.get(j).getLema())
													.getName(), "`poèet`");
							break;

						}
						j++;
					}
				} else if ("suma".equals(wordList.get(i).getLema()) == true) {

					wordList.get(i).setUsed(true);

					int j = i + 1;

					if ("objednávka".equals(wordList.get(j).getLema()) == true)
						agregAtribute = new AgregateAtribute("sum", "",
								"payment", "amount", "`suma`");

				}

			} else if (hasTag(wordList.get(i)) == true
					&& wordList.get(i).getTag().charAt(0) == 'D') {

				// prislovky hladame - mozny upgrade

			} else if (hasTag(wordList.get(i)) == true
					&& wordList.get(i).getTag().charAt(0) == 'N') {

				// cislovky hladame

				if (wordList.get(i).getLema() != null
						&& ("najmenej".equals(wordList.get(i).getSourceWord()) == true || "najviac"
								.equals(wordList.get(i).getSourceWord()) == true)) {

					wordList.get(i).setUsed(true);

					for (int j = 0; j < wordList.size(); j++) {
						if (wordList.get(j).getLema() != null
								&& (wordList.get(j).getLema()
										.startsWith("ktor") == true || wordList
										.get(j).getLema().startsWith("kto") == true)) {

							wordList.get(j).setUsed(true);

							if (hasTag(wordList.get(j))
									&& wordList.get(j).getTag().charAt(3) == 's') {
								limitAtribute = new LimitAtribute("1");
							}

							agregAtribute = new AgregateAtribute("COUNT", "*",
									null, null, "`poèet`");

							if ("najviac".equals(wordList.get(i)
									.getSourceWord()) == true)
								orderAtribute = new OrderAtribute("`poèet`",
										"DESC");
							else
								orderAtribute = new OrderAtribute("`poèet`",
										"ASC");

							if (productTypesHashMap.get(wordList.get(j + 1)
									.getLema()) != null) {

								// specialne pre typ produktu - ktory produkt
								setGroupAtribute(new GroupAtribute("",
										"product", "name"));
								break;

							} else if (wordList.get(j).getLema()
									.startsWith("kto") == true) {

								setGroupAtribute(new GroupAtribute("",
										"customer", "id_customer"));

								break;

							} else if (dbTables.get(wordList.get(j + 1)
									.getLema()) != null) {

								setGroupAtribute(new GroupAtribute("", dbTables
										.get(wordList.get(j + 1).getLema())
										.getName(), "id_"
										+ dbTables.get(
												wordList.get(j + 1).getLema())
												.getName()));

								break;

							}
						}

					}
				} else if ("viac".equals(wordList.get(i).getSourceWord()) == true
						|| "menej".equals(wordList.get(i).getSourceWord()) == true) {

					// HAVING

					if (i + 2 < wordList.size()
							&& "ako".equals(wordList.get(i + 1).getSourceWord()) == true
							&& hasTag(wordList.get(i + 2)) == true
							&& wordList.get(i + 2).getTag().charAt(0) == 'n') {

						wordList.get(i).setUsed(true);
						wordList.get(i + 1).setUsed(true);
						wordList.get(i + 2).setUsed(true);

						agregAtribute = new AgregateAtribute("COUNT", "*",
								null, null, "`poèet`");

						if ("viac".equals(wordList.get(i).getSourceWord()) == true)
							havingAtribute = new HavingAtribute("`poèet`", ">",
									wordList.get(i + 2).getSourceWord());
						else
							havingAtribute = new HavingAtribute("`poèet`", "<",
									wordList.get(i + 2).getSourceWord());

						for (int j = 0; j < wordList.size(); j++) {
							if (wordList.get(j).getLema() != null
									&& (wordList.get(j).getLema()
											.startsWith("ktor") == true || wordList
											.get(j).getLema().startsWith("kto") == true)) {

								if (productTypesHashMap.get(wordList.get(j + 1)
										.getLema()) != null) {

									// specialne pre typ produktu - ktory
									// produkt
									setGroupAtribute(new GroupAtribute("",
											"product", "name"));
									break;

								} else if ("kto".equals(wordList.get(j)
										.getLema()) == true) {

									setGroupAtribute(new GroupAtribute("",
											"customer", "id_customer"));

									break;

								} else if (dbTables.get(wordList.get(j + 1)
										.getLema()) != null) {

									setGroupAtribute(new GroupAtribute("",
											dbTables.get(
													wordList.get(j + 1)
															.getLema())
													.getName(), "id_"
													+ dbTables.get(
															wordList.get(j + 1)
																	.getLema())
															.getName()));

									break;

								}
							}
						}
					}

				}
			}
		}

		// pri cene je nutne vediet, z akeho obdobia sa ma brat, takze v
		// pripade, ak nie je udane obdobie, tak sa zobere aktualna cena

		boolean isPriceUsed = false;

		if (usedEntities.contains("product_price") == true) {
			for (WhereAtribute whereAtribute : getWhereAtributes())
				if ("product_price".equals(whereAtribute.getTableName()) == true
						&& whereAtribute.getAtributeName().contains("date") == true) {
					isPriceUsed = true;
					break;
				}

			if (isPriceUsed == false)
				getWhereAtributes().add(
						new WhereAtribute("product_price", "date_to", "is",
								"null"));

		}

		System.out.println("**************VYPIS ATRIBUTOV***********");
		for (UsedAtribute usedAtr : listOfUsedAtributes)
			System.out.println(usedAtr.getEntityName() + "-"
					+ usedAtr.getAtributeName());

		System.out
				.println("*********VYPIS POUZITYCH VSTUPOV*************************");
		for (String s : getListOfUsedEntities())
			System.out.println(s);
		System.out.println("********");

		System.out
				.println("*********VYPIS WHERE ATRIBUTOV*************************");
		for (WhereAtribute where : getWhereAtributes())
			System.out.println(where.getTableName() + "..."
					+ where.getAtributeName() + ".." + where.getOperator()
					+ where.getAtributeParameter());
		System.out.println("********");

		System.out
				.println("*********VYPIS POUZITYCH SLOV*************************");
		for (Word word : wordList)
			System.out.println(word.getSourceWord() + ".." + word.getLema()
					+ ".." + word.getTag() + ".." + word.isUsed());
		System.out.println("********");

		// HLADANIE CESTY MEDZI TABULKAMI
		List<String> nodeList = getNodeList(getListOfUsedEntities(), tableList);

		System.out.println("Vyrabanie query...");
		System.out.println();

		// VYRABANIE QUERY//
		String query = "";

		HashMap<String, String> tableAliasOfUsedTables = createTableNicknames(
				nodeList, bondDbKeys);

		if (nodeList.size() == 0)
			return null;

		// PREKLAD Z FORMALNEHO DOPYTU NA MYSQL DOPYT

		SqlQueryCreator sqlCreator = new SqlQueryCreator();
		MySQLQueryParameters mySqlQueryParams = new MySQLQueryParameters(
				nodeList, dbTables, tableAliasOfUsedTables,
				listOfUsedAtributes, agregAtribute, bondDbKeys,
				productTypesHashMap, wordList, getWhereAtributes(),
				getGroupAtribute(), havingAtribute, orderAtribute,
				limitAtribute);

		query += sqlCreator.createSelectQueryPart(mySqlQueryParams);

		query += sqlCreator.createJoinQueryPart(mySqlQueryParams);

		// VYRABANIE KLAUZUL
		query += sqlCreator.createWhereQueryPart(mySqlQueryParams);

		query += sqlCreator.createGroupQueryPart(mySqlQueryParams);

		query += sqlCreator.createHavingQueryPart(mySqlQueryParams);

		query += sqlCreator.createOrderQueryPart(mySqlQueryParams);

		query += sqlCreator.createLimitQueryPart(mySqlQueryParams);

		// MYSQL DOPYT VYTVORENY a vratime ho ako vysledok

		return query;

	}

	public List<String> getListOfUsedEntities() {
		return listOfUsedEntities;
	}

	public void setListOfUsedEntities(List<String> listOfUsedEntities) {
		this.listOfUsedEntities = listOfUsedEntities;
	}

	public List<WhereAtribute> getWhereAtributes() {
		return whereAtributes;
	}

	public void setWhereAtributes(List<WhereAtribute> whereAtributes) {
		this.whereAtributes = whereAtributes;
	}

	public GroupAtribute getGroupAtribute() {
		return groupAtribute;
	}

	public void setGroupAtribute(GroupAtribute groupAtribute) {
		this.groupAtribute = groupAtribute;
	}
}
