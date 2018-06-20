package com.bi.neorelation;

import jdk.internal.util.xml.XMLStreamException;
import org.neo4j.shell.util.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;


@Controller
public class NeoTestController {
    @Autowired
    private Search search;

    @GetMapping(value="/test_Neo4j")
    public void work()throws Exception{
        System.out.println(search.SingleNodePrefixSearch("李明",2));
        System.out.println(search.SingleNodeSuffixSearch("李芳",2));
        System.out.println(search.DoubleNodeSearch("李明","张三",2));
    }

    @RequestMapping(value = "/relation", method = {RequestMethod.GET, RequestMethod.POST})
    public String index(){
        return "graph";
    }

    @RequestMapping(value = "/search", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String search(@RequestParam("entity1")String entity1, @RequestParam("entity2")String entity2, @RequestParam("jump")String jump) throws Exception{
//        System.out.println(entity1);
//        System.out.println(entity2);
//        System.out.println(jump);
        if(jump.equals(""))
            jump = "2";
        int n_step = Integer.parseInt(jump);

        String s;
        if(entity2.equals(""))
            s = search.SingleNodePrefixSearch(entity1, n_step).toString();
        else
            s = search.DoubleNodeSearch(entity1, entity2, n_step).toString();

        System.out.println(s);
        return s;
    }

}
