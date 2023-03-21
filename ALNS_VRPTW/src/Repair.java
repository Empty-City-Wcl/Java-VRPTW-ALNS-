import java.util.Random;

public class Repair {           //修复算子类

    public static void Random_Repair(Route[] R) {           //随机插入0


        for (int i = 0;i < Parameter.Remove_Node.length;i++) {
            int RandomRouteIndex = Calculation.IntRandom(1,Parameter.VehicleNumber+1);
            for (int PositionIndex = 0;PositionIndex < R[RandomRouteIndex].N.size()-1;PositionIndex++) {
                if (R[RandomRouteIndex].N.get(PositionIndex).ReadyTime <= Parameter.Remove_Node[i].ReadyTime &&
                        Parameter.Remove_Node[i].ReadyTime <= R[RandomRouteIndex].N.get(PositionIndex+1).ReadyTime) {
                    Calculation.AddNode(R, RandomRouteIndex, PositionIndex+1, Parameter.Remove_Node[i].ID);
                    break;
                }
            }
        }

    }

    public static void Greedy_Repair(Route[] R) {               //贪婪插入1
        double Cost1;
        double Cost2;
        int RouteIndex = 0;
        int PositionIndex = 0;

        for (int m = 0;m < Parameter.Remove_Node.length;m++) {
            double Value = Parameter.MAX;                //设定一个最大值
            Cost1 = Parameter.Cost(R);                   //计算当前Cost
            for (int i = 1; i <= Parameter.VehicleNumber; i++) {      //遍历每一条线路
                for (int j = 1; j < R[i].N.size(); j++) {        //遍历当前线路中的每一个位置
                    Calculation.AddNode(R, i, j, Parameter.Remove_Node[m].ID);     //插入节点
                    Cost2 = Parameter.Cost(R);                                    //计算当前的Cost
                    if ((Cost2 - Cost1) < Value) {                            //若满足更优位置
                        Value = Cost2 - Cost1;
                        RouteIndex = i;
                        PositionIndex = j;                                      //记录位置
                    }
                    Calculation.RemoveNode(R, i, j, Parameter.Remove_Node[m].ID);          //删除节点
                }
            }
            Calculation.AddNode(R, RouteIndex, PositionIndex, Parameter.Remove_Node[m].ID);//将节点插入最优的线路位置
        }
    }

    public static void Regret_Repair(Route[] R) {                   //后悔插入2
        double Cost1;
        double Cost2;
        double Cost3;
        int RouteIndex = 0;
        int PositionIndex = 0;
        //将RegretNode列表清空
        if (Parameter.RegretNode[0].N.size() != 0) {
            Parameter.RegretNode[0].N.clear();
        }
        //将RemoveNode中的点输入到RegretNode中
        for (int i = 0;i < Parameter.Remove_NodeNumber;i++) {
            Parameter.RegretNode[0].N.add(new Node(Parameter.Remove_Node[i]));
        }

        int Number = Parameter.Remove_NodeNumber;
        while (Number > 0) {

            Cost1 = Parameter.Cost(R);          //计算当前的Cost
            for (int m = 0; m < Parameter.RegretNode[0].N.size(); m++) {
                double BestValue = Parameter.MAX;
                for (int i = 1; i <= Parameter.VehicleNumber; i++) {      //遍历每一条线路
                    for (int j = 1; j < R[i].N.size(); j++) {        //遍历当前线路中的每一个位置
                        Calculation.AddNode(R, i, j, Parameter.RegretNode[0].N.get(m).ID);     //插入节点
                        Cost2 = Parameter.Cost(R);                                    //计算当前的Cost
                        if ((Cost2 - Cost1) < BestValue) {                            //若满足更优位置
                            BestValue = Cost2 - Cost1;
                            RouteIndex = i;
                            PositionIndex = j;                                      //记录位置
                        }
                        Calculation.RemoveNode(R, i, j, Parameter.RegretNode[0].N.get(m).ID);          //删除节点
                    }
                }
                double NextValue;
                double RegretValue = Parameter.MAX;
                for (int i = 1; i <= Parameter.VehicleNumber; i++) {             //次佳插入点
                    for (int j = 1; j < R[i].N.size(); j++) {
                        Calculation.AddNode(R, i, j, Parameter.RegretNode[0].N.get(m).ID);
                        Cost2 = Parameter.Cost(R);
                        NextValue = Cost2 - Cost1;
                        if ((NextValue - BestValue) < RegretValue && (NextValue - BestValue) != 0) {
                            RegretValue = NextValue - BestValue;
                        }
                        Calculation.RemoveNode(R, i, j, Parameter.RegretNode[0].N.get(m).ID);          //删除节点
                    }
                }
                Parameter.RegretNode[0].N.get(m).Regret_Cost = RegretValue;
                Parameter.RegretNode[0].N.get(m).R = RouteIndex;
                Parameter.RegretNode[0].N.get(m).Position = PositionIndex;                              //记录相关指标
            }

            //找到当前后悔值最大的节点
            int RemoveID = 0;
            double Delta = Parameter.MIN;
            for (int i = 0;i < Parameter.RegretNode[0].N.size();i++) {
                if (Parameter.RegretNode[0].N.get(i).Regret_Cost > Delta) {
                    Delta = Parameter.RegretNode[0].N.get(i).Regret_Cost;
                    RemoveID = i;                   //记录当前最大后悔值的节点并移除
                }
            }

            //将找到的当前后悔值最大的节点加入
            Calculation.AddNode(R, Parameter.RegretNode[0].N.get(RemoveID).R, Parameter.RegretNode[0].N.get(RemoveID).Position, Parameter.RegretNode[0].N.get(RemoveID).ID);
            //将移除的节点在RegretNode中移除
            Parameter.RegretNode[0].N.remove(RemoveID);
            Number--;
        }
    }
}
