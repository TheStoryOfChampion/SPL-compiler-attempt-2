package Parser;
import java.util.ArrayList;

public class TreeNode {
    int id;
    String name;
    String type;//..............................TERMINAL OR NONTERMINAL...........................................
    ArrayList<TreeNode> children = new ArrayList<>();
    int newScope=0;
    int ScopeId=0;

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
