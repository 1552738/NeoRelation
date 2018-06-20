package com.bi.neorelation;


//import org.neo4j.ogm.json.JSONArray;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import org.neo4j.shell.util.json.JSONArray;
import org.neo4j.shell.util.json.JSONObject;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;
import com.bi.neorelation.Relation;

import static org.neo4j.driver.v1.Values.parameters;

@Component
public class Search {
    Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "123456"));
    private Session session = driver.session();
    private Map<Long, Node> nodesMap = new HashMap<>();
    private JSONObject Node, Edge, Result;
    private JSONArray Nodes, Edges;

    public JSONObject SingleNodePrefixSearch(String node_name, int n_step) throws Exception, SQLException {
        Set<String> nodesSet = new HashSet<String>();
        Set<Relation> edgesSet=new HashSet<Relation>();
        Nodes = new JSONArray();
        Edges = new JSONArray();
        Result = new JSONObject();
        try {
            String sql = "match p=(node1:Person{name:{node_name}})";
            if (n_step == 0) {
                sql = sql + "-[*]->() return p";
            } else {
                for (int i = 1; i < n_step; ++i) sql = sql + "-->()";
                sql = sql + "-->() return p";
            }
            StatementResult result = session.run(sql, parameters("node_name", node_name));
            while (result.hasNext()) {
                Record record = result.next();
                List<Value> values = record.values();
                for (Value value : values) {
                    if (value.type().name().equals("PATH")) {
                        Path p = value.asPath();
                        //取出路径中所有点的信息
                        Iterable<Node> nodes = p.nodes();
                        for (Node node : nodes) {
                            nodesMap.put(node.id(), node);
                            nodesSet.add(node.get("name").asString());
                        }
                        //取出路径中所有关系(点为Id)
                        Iterable<Relationship> relationships = p.relationships();
                        for (Relationship relationship : relationships) {
                            Long startID = relationship.startNodeId();
                            Long endID = relationship.endNodeId();
                            String rType = relationship.type();
                            Node startObject = nodesMap.get(startID);
                            Node endObject = nodesMap.get(endID);
                            edgesSet.add(new Relation(startObject.get("name").asString(),endObject.get("name").asString(),rType));
//                            asMap 相当于 节点的properties属性信息
//                            System.out.println(nodesMap.get(startID).keys());
//                            System.out.println(nodesMap.get(startID).values());
//                            System.out.println(nodesMap.get(startID).get("age"));
//                            System.out.println(nodesMap.get(startID).asMap() + "-" + rType + "-"+nodesMap.get(endID).asMap());
                        }
                    }
                    System.out.println();
                }
            }
            for (String name : nodesSet) {
                Node = new JSONObject();
                Node.put("name", name);
                Nodes.put(Node);
            }
            for (Relation relation:edgesSet){
                Edge = new JSONObject();
                Edge.put("source", relation.getSource());
                Edge.put("target", relation.getTarget());
                Edge.put("type", relation.getType());
                Edges.put(Edge);
            }
            Result.put("nodes", Nodes);
            Result.put("edges", Edges);
            return Result;
        } catch (Exception e) {
            System.err.println(e.getClass() + "," + e.getMessage());
        }
        return null;
    }

    public JSONObject SingleNodeSuffixSearch(String node_name,int n_step) throws Exception,SQLException {
        Set<String> nodesSet = new HashSet<String>();
        Set<Relation> edgesSet = new HashSet<Relation>();
        Nodes = new JSONArray();
        Edges = new JSONArray();
        Result = new JSONObject();
        try {
            String sql = "match p=()";
            if (n_step == 0) {
                sql = sql + "-[*]->(node1:Person{name:{node_name}}) return p";
            } else {
                for (int i = 1; i < n_step; ++i) sql = sql + "-->()";
                sql = sql + "-->(node1:Person{name:{node_name}}) return p";
            }
            StatementResult result = session.run(sql, parameters("node_name", node_name));
            while (result.hasNext()) {
                Record record = result.next();
                List<Value> values = record.values();
                for (Value value : values) {
                    if (value.type().name().equals("PATH")) {
                        Path p = value.asPath();
                        //取出路径中所有点的信息
                        Iterable<Node> nodes = p.nodes();
                        for (Node node : nodes) {
                            nodesMap.put(node.id(), node);
                            nodesSet.add(node.get("name").asString());
                        }
                        //取出路径中所有关系(点为Id)
                        Iterable<Relationship> relationships = p.relationships();
                        for (Relationship relationship : relationships) {
                            Long startID = relationship.startNodeId();
                            Long endID = relationship.endNodeId();
                            String rType = relationship.type();
                            Node startObject = nodesMap.get(startID);
                            Node endObject = nodesMap.get(endID);
                            edgesSet.add(new Relation(startObject.get("name").asString(), endObject.get("name").asString(), rType));
//                            asMap 相当于 节点的properties属性信息
//                            System.out.println(nodesMap.get(startID).keys());
//                            System.out.println(nodesMap.get(startID).values());
//                            System.out.println(nodesMap.get(startID).get("age"));
//                            System.out.println(nodesMap.get(startID).asMap() + "-" + rType + "-"+nodesMap.get(endID).asMap());
                        }
                    }
                    System.out.println();
                }
            }
            for (String name : nodesSet) {
                Node = new JSONObject();
                Node.put("name", name);
                Nodes.put(Node);
            }
            for (Relation relation : edgesSet) {
                Edge = new JSONObject();
                Edge.put("source", relation.getSource());
                Edge.put("target", relation.getTarget());
                Edge.put("type", relation.getType());
                Edges.put(Edge);
            }
            Result.put("nodes", Nodes);
            Result.put("edges", Edges);
            return Result;
        } catch (Exception e) {
            System.err.println(e.getClass() + "," + e.getMessage());
        }
        return null;
    }

    public JSONObject DoubleNodeSearch(String node_name1,String node_name2,int n_step)throws Exception,SQLException{
        Set<String> nodesSet = new HashSet<String>();
        Set<Relation> edgesSet=new HashSet<Relation>();
        Nodes = new JSONArray();
        Edges = new JSONArray();
        Result = new JSONObject();
        try{
            String sql="match p=(user1:Person{name:{node_name1}})";
            if (n_step==0){
                sql = sql + "-[*]->(user2:Person{name:{node_name2}}) return p";
            }
            else {
                for (int i=1;i<n_step;++i)sql=sql+"-->()";
                sql = sql + "-->(user2:Person{name:{node_name2}}) return p";
            }
            StatementResult result = session.run(sql,parameters("node_name1", node_name1,"node_name2",node_name2));
            while (result.hasNext()) {
                Record record = result.next();
                List<Value> values = record.values();
                for (Value value : values) {
                    if (value.type().name().equals("PATH")) {
                        Path p = value.asPath();
                        //取出路径中所有点的信息
                        Iterable<Node> nodes = p.nodes();
                        for (Node node : nodes) {
                            nodesMap.put(node.id(), node);
                            nodesSet.add(node.get("name").asString());
                        }
                        //取出路径中所有关系(点为Id)
                        Iterable<Relationship> relationships = p.relationships();
                        for (Relationship relationship : relationships) {
                            Long startID = relationship.startNodeId();
                            Long endID = relationship.endNodeId();
                            String rType = relationship.type();
                            Node startObject = nodesMap.get(startID);
                            Node endObject = nodesMap.get(endID);
                            edgesSet.add(new Relation(startObject.get("name").asString(), endObject.get("name").asString(), rType));
//                            asMap 相当于 节点的properties属性信息
//                            System.out.println(nodesMap.get(startID).keys());
//                            System.out.println(nodesMap.get(startID).values());
//                            System.out.println(nodesMap.get(startID).get("age"));
//                            System.out.println(nodesMap.get(startID).asMap() + "-" + rType + "-"+nodesMap.get(endID).asMap());
                        }
                    }
                    System.out.println();
                }
            }
            for (String name : nodesSet) {
                Node = new JSONObject();
                Node.put("name", name);
                Nodes.put(Node);
            }
            for (Relation relation : edgesSet) {
                Edge = new JSONObject();
                Edge.put("source", relation.getSource());
                Edge.put("target", relation.getTarget());
                Edge.put("type", relation.getType());
                Edges.put(Edge);
            }
            Result.put("nodes", Nodes);
            Result.put("edges", Edges);
            return Result;
        } catch (Exception e) {
            System.err.println(e.getClass() + "," + e.getMessage());
        }
        return null;
    }

    public void shortEstPath() throws Exception {
//        try {
//            String cmdSql = "match p=(user1:User{name:\"李明\"})-[*]->(user2:User{name:\"李芳\"}) return p";
//            StatementResult result = session.run(cmdSql);
//            while (result.hasNext()) {
//                Record record = result.next();
//                List<Value> values = record.values();
//                Map<Long, Node> nodesMap = new HashMap<>();
//                for (Value value : values) {
//                    if (value.type().name().equals("PATH")) {
//                        //取出路径
//                        Path p = value.asPath();
//                        System.out.println("小讯和小锐之间的关系最短路径长度为：" + p.length());
//                        System.out.println("====================================");
//                        //取出路径中所有点的信息
//                        Iterable<Node> nodes = p.nodes();
//                        for (Node node : nodes) {
//                            nodesMap.put(node.id(), node);
//                        }
//
//                        /**
//                         * 打印最短路径里面的关系 == 关系包括起始节点的ID和末尾节点的ID，以及关系的type类型
//                         */
//                        //取出路径中所有关系(点为Id)
//                        Iterable<Relationship> relationships = p.relationships();
//                        for (Relationship relationship : relationships) {
//                            Long startID = relationship.startNodeId();
//                            Long endID = relationship.endNodeId();
//                            String rType = relationship.type();
//                            /**
//                             * asMap 相当于 节点的properties属性信息
//                             */
//                            System.out.println(
//                                    nodesMap.get(startID).asMap() + "-" + rType + "-"
//                                            + nodesMap.get(endID).asMap());
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            System.err.println(e.getClass() + "," + e.getMessage());
//        }
    }

}

