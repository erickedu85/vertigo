package sumgra.otil;


import com.gs.collections.impl.map.mutable.primitive.ShortObjectHashMap;

import sumgra.data.Settings;

import com.gs.collections.impl.list.mutable.primitive.IntArrayList;
import com.gs.collections.impl.list.mutable.FastList;

public class OtilNode {
	
	public OtilNode parent;
	public ShortObjectHashMap<OtilNode> children;
	public short label;
	public IntArrayList vertexIds_in;
	
	public OtilNode(OtilNode parent, short label){
		this.parent = parent;
		this.label = label;
		vertexIds_in = new IntArrayList();
		children = null;
	}

//	public void addIdVertex(int id, int direction){
//		if (direction == Settings.IN)
//			vertexIds_in.add(id);
//		else
//			vertexIds_out.add(id);
//	}
	
	public void addIdVertex(int id){
		vertexIds_in.add(id);
	}
	
	
	public boolean addChild(OtilNode child){
		if (children == null)  children = new ShortObjectHashMap<OtilNode>();
		if (children.containsKey(child.label)){
			return false;
		}else{
			children.put(child.label, child);
		}
		return true;
	}
	
	public OtilNode getChild(short label){
		if (children == null) 
			return null;
		else if (children.containsKey(label)){
			return (OtilNode) children.get(label);
		}else
			return null;
	}
	
	public ShortObjectHashMap<OtilNode> getChildren(){
		return children;
	}
	
}
