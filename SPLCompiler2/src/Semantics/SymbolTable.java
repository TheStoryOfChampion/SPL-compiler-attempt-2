package Semantics;


public class SymbolTable{

    int NodeId;
    String NodeName;
    int ScopeId;
    String ScopeName;

    Boolean called=false;
    int parentID;
    SymbolTable(int NodeId, String NodeName, int ScopeId, String ScopeName){
        this.NodeId = NodeId;
        this.NodeName = NodeName;
        this.ScopeId = ScopeId;
        this.ScopeName = ScopeName;
    }
}
