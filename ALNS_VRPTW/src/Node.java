public class Node {
    int ID;
    double demand;
    double ReadyTime;
    double ServiceTime;
    double Duetime;
    double x;
    double y;
    int R;                  //所属节点路线
    int Position;
    double Regret_Cost;

    public Node() {
        this.ID = 0;
        this.demand = 0;
        this.ReadyTime = 0;
        this.ServiceTime = 0;
        this.Duetime = 0;
        this.x = 0;
        this.y = 0;
        this.R = 0;
        this.Position = 0;
        this.Regret_Cost = 0.0;
    }

    public Node(Node N) {
        this.ID = N.ID;
        this.demand = N.demand;
        this.ReadyTime = N.ReadyTime;
        this.ServiceTime = N.ServiceTime;
        this.Duetime = N.Duetime;
        this.x = N.x;
        this.y = N.y;
        this.R = N.R;
        this.Position = N.Position;
        this.Regret_Cost = N.Regret_Cost;
    }
}
