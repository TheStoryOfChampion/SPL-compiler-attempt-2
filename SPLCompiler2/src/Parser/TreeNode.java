package Parser;
import java.util.ArrayList;

public class TreeNode {
    public int id;
    public String name;
    public String type;//..............................TERMINAL OR NONTERMINAL...........................................
    public ArrayList<TreeNode> children = new ArrayList<>();
    public int newScope=0;
    public int ScopeId=0;

    void setScopeID(int scopeID){
        this.ScopeId=scopeID;
    }

    int getScopeID(){
        return this.ScopeId;
    }

    void setNewScope(int newScope){
        this.newScope=newScope;
    }

    int getNewScope(){
        return this.newScope;
    }

    public TreeNode(String name){
        this.name=name;
    }

    public TreeNode(String name, String type) {
        this.type=type;
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void addChild(TreeNode child){
        this.children.add(child);
    }
}
