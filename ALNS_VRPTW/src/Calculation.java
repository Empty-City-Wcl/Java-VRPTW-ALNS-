import java.util.Random;

import static java.lang.Math.*;

public class Calculation {                              //计算两个节点之间的欧式距离
    public static double Euclidean(Node n1, Node n2) {
        return sqrt(pow((n1.x - n2.x), 2) + pow((n1.y - n2.y), 2));
    }

    public static double NodeRelated(Node n1, Node n2) {
        double a = 0.0;
        a = 1 * (Parameter.DisMatrix[n1.ID][n2.ID]) +
                0.2 * (abs(n1.ReadyTime - n2.ReadyTime) + abs(n1.Duetime - n2.Duetime)) +
                1 * (abs(n1.demand - n2.demand));
        return a;
    }

    public static void CalDistanceMatrix() {            //生成客户节点之间的距离矩阵
        for (int i = 1; i <= Parameter.NodeNumber + 1; i++) {
            for (int j = 1; j <= Parameter.NodeNumber + 1; j++) {
                Parameter.DisMatrix[i][j] = Euclidean(Parameter.N[i], Parameter.N[j]);
            }
        }
    }

    public static void CalRelatedMatrix() {             //计算节点的相关性矩阵
        for (int i = 2; i <= Parameter.NodeNumber + 1; i++) {
            for (int j = 2; j <= Parameter.NodeNumber + 1;j++) {
                Parameter.RelatedMatrix[i][j] = NodeRelated(Parameter.N[i], Parameter.N[j]);
            }
        }
    }

    public static void RemoveNode(Route[] R, int Route_index, int Position_index, int Node_index) {
        R[Route_index].Load = R[Route_index].Load - Parameter.N[Node_index].demand;       //减去节点需求
        R[Route_index].N.remove(Position_index);                                                    //删除节点
        Route.UPDis(R[Route_index]);
        Route.UPSubT(R[Route_index]);                                                               //更新违反时间窗量
        R[Route_index].N.get(Position_index).R = Route_index;                                       //更新所属路径
        Parameter.N[Node_index].R = 0;
    }

    public static void AddNode(Route[] R, int Route_index, int Position_index, int Node_index) {
        R[Route_index].Load = R[Route_index].Load + Parameter.N[Node_index].demand;
        R[Route_index].N.add(Position_index, Parameter.N[Node_index]);
        //更新路径长度的优化方法
        Route.UPDis(R[Route_index]);                                                                //更新距离
        Route.UPSubT(R[Route_index]);                                                               //更新违反时间窗量
        R[Route_index].N.get(Position_index).R = Route_index;                                       //更新所属路径
        Parameter.N[Node_index].R = Route_index;
    }

    public static void selectSol_Roulette() {               //采用轮盘赌的方式选择破坏和修复算子
        double Destory_All = 0.0;
        double Repair_All = 0.0;
        for (int i = 0;i < Parameter.WDestory.length;i++) {
            Destory_All += Parameter.WDestory[i];
        }

        for (int i = 0;i < Parameter.WRepair.length;i++) {
            Repair_All += Parameter.WRepair[i];
        }
        //破坏算子选择
        double a = Calculation.DoubleRandom(0,Destory_All);
        if (a < Parameter.WDestory[0]) {
            Parameter.Destory_Selection = 0;
        } else if (a >= Parameter.WDestory[0] && a < (Parameter.WDestory[1]+Parameter.WDestory[0])) {
            Parameter.Destory_Selection = 1;
        } else if (a >= Parameter.WDestory[1]) {
            Parameter.Destory_Selection = 2;
        }

        //修复算子选择
        double b = Calculation.DoubleRandom(0,Repair_All);
        if (b < Parameter.WRepair[0]) {
            Parameter.Repair_Selection = 0;
        } else if (b >= Parameter.WRepair[0] && b < (Parameter.WRepair[1]+Parameter.WRepair[0])) {
            Parameter.Repair_Selection = 1;
        } else if (b >= Parameter.WRepair[1]) {
            Parameter.Repair_Selection = 2;
        }
    }

    public static void SelectAlgorithm() {

        //采用选中的算子破坏原有线路解
        if (Parameter.Destory_Selection == 0) {
            Destory.Random_Destory(Parameter.new_Route);
        } else if (Parameter.Destory_Selection == 1) {
            Destory.Greedy_Destory(Parameter.new_Route);
        } else if (Parameter.Destory_Selection == 2) {
            Destory.Shaw_Destory(Parameter.new_Route);
        }
        Parameter.DestoryUseTime[Parameter.Destory_Selection] += 1;     //次数+1
        System.out.println("采用破坏算子" + Parameter.Destory_Selection);

        //采用选中的算子修复原有路线
        //Repair.Greedy_Repair(Parameter.new_Route);
        if (Parameter.Repair_Selection == 0) {
            Repair.Random_Repair(Parameter.new_Route);
        } else if (Parameter.Repair_Selection == 1) {
            Repair.Greedy_Repair(Parameter.new_Route);
        } else if (Parameter.Repair_Selection == 2) {
            Repair.Regret_Repair(Parameter.new_Route);
        }
        Parameter.RepairUseTime[Parameter.Repair_Selection] += 1;       //次数+1
        System.out.println("采用修复算子" + Parameter.Repair_Selection);
    }

    public static void CalScore() {     //计算权重并更新得分
        //计算破坏-修复后的Cost
        Parameter.New_Value = Parameter.Cost(Parameter.new_Route);
        if (Parameter.New_Value <= Parameter.Local_Value) {                 //首先与当前最优解比较，若破坏-修复后的新解更优，则更新当前解
            //清空当前线路
            Calculation.ClearRoute(Parameter.R);
            //更新当前路线与当前最优Cost
            Calculation.CloneRoute(Parameter.R, Parameter.new_Route);
            //更新算子权重
            Parameter.WDestory[Parameter.Destory_Selection] += 1.3;
            Parameter.WRepair[Parameter.Repair_Selection] += 1.3;
            if (Parameter.New_Value <= Parameter.Best_Value) {
                //清空最优解线路
                Calculation.ClearRoute(Parameter.Best_Route);
                //更新最优解线路与Cost
                Calculation.CloneRoute(Parameter.Best_Route, Parameter.new_Route);
                Parameter.Best_Value = Parameter.New_Value;
                //再次更新算子权重
                Parameter.WDestory[Parameter.Destory_Selection] += 0.8;
                Parameter.WRepair[Parameter.Repair_Selection] += 0.8;
            }
        } else if (Parameter.New_Value > Parameter.Local_Value) {           //若新解更差
            //对于相对劣解，通过退火模拟的方法以概率接受
            if (DoubleRandom(0,1) <= 0.15) {            //若条件成立，接受劣解
                //清空当前线路
                Calculation.ClearRoute(Parameter.R);
                //更新当前路线与当前最优Cost
                Calculation.CloneRoute(Parameter.R, Parameter.new_Route);
                //Parameter.Local_Value = Parameter.New_Value;
                //更新算子权重
                Parameter.WDestory[Parameter.Destory_Selection] += 0.5;
                Parameter.WRepair[Parameter.Repair_Selection] += 0.5;
            } else if (DoubleRandom(0,1) > 0.15) {      //若条件不成立，则维持原解
                Parameter.WDestory[Parameter.Destory_Selection] += 0;
                Parameter.WRepair[Parameter.Repair_Selection] += 0;
            }
        }
    }

    public static void ClearRoute(Route[] R) {
        for (int i = 1;i < Parameter.NodeNumber + 5;i++) {
            if (R[i].N.size() != 0) {
                R[i].N.clear();
            }
            R[i].Load = 0;
            R[i].SubT = 0;
            R[i].Dis = 0;
        }
    }

    public static void CloneRoute(Route[] R1, Route[] R2) {                 //将R2克隆给R1 R2→R1
        for (int i = 1;i < Parameter.NodeNumber + 5;i++) {                //遍历每一条线路
            for (int j = 0;j < R2[i].N.size();j++) {           //遍历线路中的每一个节点
                R1[i].N.add(R2[i].N.get(j));
            }
            R1[i].SubT = R2[i].SubT;
            R1[i].Dis = R2[i].Dis;
            R1[i].Load = R2[i].Load;
        }
    }

    public static Boolean CompareRoute(Route[] R1, Route[] R2) {   //将Rq和R2比较
        for (int i = 1;i <= Parameter.VehicleNumber;i++) {          //遍历每一条线路
            for (int j = 1;j < R1[i].N.size();j++) {                //遍历每一个节点的位置
                if (R1[i].N.get(j).ID != R2[i].N.get(j).ID) {       //若解不同
                    return false;
                }
            }
        }
        return true;
    }

    public static int IntRandom(int min, int max) {
        Random r = new Random();
        int a = r.nextInt(min,max);
        return a;
    }
    public static double DoubleRandom(double min, double max) {
        Random r = new Random();
        double a = r.nextDouble(min,max);
        return a;
    }

}
