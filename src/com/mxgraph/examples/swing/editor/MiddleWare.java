package com.mxgraph.examples.swing.editor;

import java.util.ArrayList;
import java.util.HashMap;

import com.mxgraph.model.mxCell;

public class MiddleWare {

    // The single instance that is used as MiddleWare
    private static MiddleWare instance = new MiddleWare();
    // Map every port to their parent
    public HashMap<String, mxCell> portParent = new HashMap<>();

    public HashMap<String, String> portSemantic = new HashMap<>();

    public ArrayList connLst = new ArrayList();

    public HashMap<String, String> connType = new HashMap<>();

    public HashMap getPortParent() {
        return portParent;

    }
    public String srcPortId;

    public String srcPortParentId;

    public String tarPortId;

    public ArrayList firstObjForObjToObjCardinality  = new ArrayList();

    public ArrayList secondObjForObjToObjCardinality = new ArrayList();

    public ArrayList refActForObjToObjCardinality = new ArrayList();

    public ArrayList cardNumForObjToObjCardinality  = new ArrayList();

    public ArrayList RefActForActToObjCardinalityForAct = new ArrayList();

    public ArrayList objForActToObjCardinality = new ArrayList();

    public ArrayList cardNumForActToObjCardinality  = new ArrayList();

    public ArrayList firstActForActOrder = new ArrayList();

    public ArrayList secondActForActOrder = new ArrayList();

    public ArrayList refObjForActOrder  = new ArrayList();

    public ArrayList orderTypeForActOrder = new ArrayList();

    public ArrayList firstRefObjForActOrder = new ArrayList();

    public ArrayList secondRefObjForActOrder = new ArrayList();

    public ArrayList firstTimeForActOrder = new ArrayList();

    public ArrayList secondTimeForActOrder = new ArrayList();

    /**
     constructor method
    **/
    private MiddleWare(){

    }

    public static MiddleWare getInstance(){
        return instance;
    }

    /**
     save the parent of the port
     **/
    public void updatePortParent(String id, mxCell parent) {
        this.portParent.put(id, parent);
    }

    /**
     save the semantic of the port
     **/
    public void updatePortSemantic(String id, String semant) {
        this.portSemantic.put(id, semant);
    }

    // save the connection that includes both source id and target id
    public void updatePortConn(String srcId, String tarId){
        ArrayList conn = new ArrayList<>();
        conn.add(srcId);
        conn.add(tarId);
        this.connLst.add(conn);
    }

    public ArrayList getPortConn(){
        return connLst;
    }

    public String getSrcPortId() {
        return srcPortId;
    }

    public String getSrcPortParentId() {
        return srcPortParentId;
    }

    public void setSrcPortId(String srcPortId) {
        this.srcPortId = srcPortId;
    }

    public void setSrcPortParentId(String srcPortParentId) {
        this.srcPortParentId = srcPortParentId;
    }

    public String getTarPortId() {
        return tarPortId;
    }

    public void setTarPortId(String tarPortId) {
        this.tarPortId = tarPortId;
    }

    public void setConnType(String id, String selectedConnType) {
        connType.put(id,selectedConnType);
    }

    /**
     * the entity exist list
     */

    /**
     * the obj-to-obj cardinality
     */
    public void addFirstObjForObjToObjCardinality(String object){
        this.firstObjForObjToObjCardinality.add(object);
    }

    public void addSecondObjForObjToObjCardinality(String object){
        this.secondObjForObjToObjCardinality.add(object);
    }

    public void addRefActForObjToObjCardinality(String refActivity){
        this.refActForObjToObjCardinality.add(refActivity);
    }

    public void addCardForObjToObjCardinality(String refActivity){
        this.cardNumForObjToObjCardinality.add(refActivity);
    }

    /**
     * the act-to-obj cardinality
     */
    public void addActForActToObjCardinality(String refActivity){
        this.RefActForActToObjCardinalityForAct.add(refActivity);
    }

    public void addObjForActToObjCardinality(String object){
        this.objForActToObjCardinality.add(object);
    }

    public void addCardForActToObjCardinality(String refActivity){
        this.cardNumForActToObjCardinality.add(refActivity);
    }

    /**
     * the order relation for two activities (with one reference obj)
     */
    public void addFirstActForActOrder(String refAct){
        this.firstActForActOrder.add(refAct);
    }

    public void addSecondActForActOrder(String targetAct){
        this.secondActForActOrder.add(targetAct);
    }

    public void addRefObjForActOrder(String refObj){
        this.refObjForActOrder.add(refObj);
    }

    public void addOrderTypeForActOrder(String orderType){
        this.orderTypeForActOrder.add(orderType);
    }

    public void addFirstRefTimeForActOrder(String refFirstTimeStamp){
        this.firstTimeForActOrder.add(refFirstTimeStamp);
    }

    public void addSecondRefTimeForActOrder(String refSecondTimeStamp){
        this.secondTimeForActOrder.add(refSecondTimeStamp);
    }

    /**
     * the order relation for two activities (with two reference obj)
     */
    public void addFirstActivityForActOrder(String refAct){
        this.firstActForActOrder.add(refAct);
    }

    public void addSecondActivityForActOrder(String targetAct){
        this.secondActForActOrder.add(targetAct);
    }

    public void addFirstRefObjForActOrder(String refObj){
        this.firstRefObjForActOrder.add(refObj);
    }

    public void addSecondRefObjForActOrder(String refObj){
        this.secondRefObjForActOrder.add(refObj);
    }

    public void getCardinality(){
        System.out.println(this.firstObjForObjToObjCardinality);
    }
}
