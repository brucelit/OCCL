package com.mxgraph.examples.swing.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.jgraph.visualization.ProMJGraphPanel;
import org.processmining.ocel.ocelobjects.OcelEvent;
import org.processmining.ocel.ocelobjects.OcelEventLog;

@Plugin(name = "(0) Visualize OCEL", 
returnLabels = { "OCEL Table" },
returnTypes = { JComponent.class },
parameterLabels = { "Object-Centric Event Log" },
help = "Object-Centric Event Log",
userAccessible = true)
@Visualizer
public class OCELTable{
    protected JPanel root;
    protected ProMJGraphPanel panel;

    class ColumnComparator implements Comparator<String[]>
    {
        public int compare(Date[] array, Date[] anotherArray)
        {
            return array[0].compareTo( anotherArray[0] );
        }

		public int compare(String[] o1, String[] o2) {
			// TODO Auto-generated method stub
			return 0;
		}
    };
    
    @PluginVariant(requiredParameterLabels = {0})
    public JComponent runOCEL(UIPluginContext context, OcelEventLog ocel) throws ParseException {
		   	
		root = new JPanel();
		root.setBorder(BorderFactory.createEmptyBorder());
		root.setBackground(new Color(100, 100, 100));
		root.setLayout(new BorderLayout());

	    Object [] title = {"Event Id","Timestamp","Activity","relatedObjects"};

	    Object [] data = {};
	    
        Vector titlesV = new Vector(); // save title
        Vector<Vector> dataV = new Vector<Vector>(); // save data

//        Vector<Vector> dataV = new Vector<>(); // save data

        for (int i=0; i<title.length;i++){
            titlesV.add(title[i]);
        }
        
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd'T'hh:mm:ss");
        
		
		for (OcelEvent ev : ocel.events.values()) {
			Vector<Object> t = new Vector<Object>();
			t.add(ev.id);
			t.add(ft.format(ev.timestamp));
			t.add(ev.activity);
			t.add(ev.relatedObjectsIdentifiers.toString());
//			t.add(ev.object.toString());
			dataV.add(t);	
		}
		
		    
	    TableModel model = new DefaultTableModel(dataV, titlesV);  
        JTable table = new JTable(model);  
        TableColumn col0 = table.getColumn(title[0]);
        col0.setMaxWidth(120);
        TableColumn col1 = table.getColumn(title[1]);
        col1.setMinWidth(150);
        col1.setMaxWidth(150);
        TableColumn col2 = table.getColumn(title[2]);
        col2.setMinWidth(250);
        col2.setMaxWidth(250);
        
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
        table.setRowSorter(sorter); 
        List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
	    sorter.setSortKeys(sortKeys);
	    
	    JScrollPane jsp = new JScrollPane(table);
        root.add(jsp);
        return root;
        
    }
}
