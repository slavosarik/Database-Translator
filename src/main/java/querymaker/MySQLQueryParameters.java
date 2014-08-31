package querymaker;

import java.util.HashMap;
import java.util.List;

import parser.Word;
import xmlLoader.Table;

public class MySQLQueryParameters {

	private List<String> nodeList;
	private HashMap<String, Table> dbTables;
	private HashMap<String, String> tableAliasOfUsedTables;
	private AgregateAtribute agregAtribute;
	private HashMap<String, String> bondDbKeys;
	private HashMap<String, String> productTypesHashMap;
	private List<Word> wordList;
	private List<WhereAtribute> whereAtributes;
	private GroupAtribute groupAtribute;
	private OrderAtribute orderAtribute;
	private LimitAtribute limitAtribute;
	private List<UsedAtribute> usedAtributes;
	private HavingAtribute havingAtribute;

	public List<String> getNodeList() {
		return nodeList;
	}

	public void setNodeList(List<String> nodeList) {
		this.nodeList = nodeList;
	}

	public HashMap<String, Table> getDbTabulky() {
		return dbTables;
	}

	public void setDbTabulky(HashMap<String, Table> dbTabulky) {
		this.dbTables = dbTabulky;
	}

	public HashMap<String, String> getSkratkyPouzitychTabuliek() {
		return tableAliasOfUsedTables;
	}

	public void setSkratkyPouzitychTabuliek(
			HashMap<String, String> skratkyPouzitychTabuliek) {
		this.tableAliasOfUsedTables = skratkyPouzitychTabuliek;
	}

	public AgregateAtribute getAgregAtribute() {
		return agregAtribute;
	}

	public void setAgregAtribute(AgregateAtribute agregAtribute) {
		this.agregAtribute = agregAtribute;
	}

	public HashMap<String, String> getVazbyDBKluce() {
		return bondDbKeys;
	}

	public void setVazbyDBKluce(HashMap<String, String> vazbyDBKluce) {
		this.bondDbKeys = vazbyDBKluce;
	}

	public HashMap<String, String> getProductTypesHashMap() {
		return productTypesHashMap;
	}

	public void setProductTypesHashMap(
			HashMap<String, String> productTypesHashMap) {
		this.productTypesHashMap = productTypesHashMap;
	}

	public List<Word> getWordList() {
		return wordList;
	}

	public void setWordList(List<Word> wordList) {
		this.wordList = wordList;
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

	public OrderAtribute getOrderAtribute() {
		return orderAtribute;
	}

	public void setOrderAtribute(OrderAtribute orderAtribute) {
		this.orderAtribute = orderAtribute;
	}

	public LimitAtribute getLimitAtribute() {
		return limitAtribute;
	}

	public void setLimitAtribute(LimitAtribute limitAtribute) {
		this.limitAtribute = limitAtribute;
	}

	public List<UsedAtribute> getUsedAtributes() {
		return usedAtributes;
	}

	public void setUsedAtributes(List<UsedAtribute> usedAtributes) {
		this.usedAtributes = usedAtributes;
	}

	public HavingAtribute getHavingAtribute() {
		return havingAtribute;
	}

	public void setHavingAtribute(HavingAtribute havingAtribute) {
		this.havingAtribute = havingAtribute;
	}

	public MySQLQueryParameters(List<String> nodeList,
			HashMap<String, Table> dbTabulky,
			HashMap<String, String> skratkyPouzitychTabuliek,
			List<UsedAtribute> listOfUsedAtributes,
			AgregateAtribute agregAtribute,
			HashMap<String, String> vazbyDBKluce,
			HashMap<String, String> productTypesHashMap, List<Word> wordList,
			List<WhereAtribute> whereAtributes, GroupAtribute groupAtribute,
			HavingAtribute havingAtribute, OrderAtribute orderAtribute,
			LimitAtribute limitAtribute) {

		this.nodeList = nodeList;
		this.dbTables = dbTabulky;
		this.tableAliasOfUsedTables = skratkyPouzitychTabuliek;
		this.agregAtribute = agregAtribute;
		this.bondDbKeys = vazbyDBKluce;
		this.productTypesHashMap = productTypesHashMap;
		this.wordList = wordList;
		this.whereAtributes = whereAtributes;
		this.groupAtribute = groupAtribute;
		this.orderAtribute = orderAtribute;
		this.limitAtribute = limitAtribute;
		this.usedAtributes = listOfUsedAtributes;
		this.havingAtribute = havingAtribute;

	}

}
