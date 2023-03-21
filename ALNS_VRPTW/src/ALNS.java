import java.util.Random;

public class ALNS {             //自适应大规模邻域算法

    public static void ALNS() {

        Parameter.Best_Value = Parameter.MAX;
        int Iteration = 1;
        double T;
        double Down = 0.99;
        int CountSameNumber = 0;            //计算线路相同的次数

        while (Iteration <= Parameter.MaxIteration) {
            System.out.println("------------------");
            System.out.println(Iteration);
            System.out.println("------------------");
            T = 100.0;

            for (int i = 0; i < 3;i++) {
                Parameter.WRepair[i] = 1;
                Parameter.RepairUseTime[i] = 0;
            }
            for (int i = 0; i < 3;i++) {
                Parameter.WDestory[i] = 1;
                Parameter.DestoryUseTime[i] = 0;
            }
            int Count = 0;
            while (T > 5) {
                Count++;
                System.out.println("------------------");
                System.out.println(Iteration + "-"+ Count);

                //记录破坏-修复前线路的Cost
                if (Iteration >=20) {
                    if (Calculation.CompareRoute(Parameter.R, Parameter.new_Route) == true && Parameter.Local_Value == Parameter.New_Value) {
                        //若解集相同，则相同计数+1
                        CountSameNumber++;
                    }
                }

                Parameter.Local_Value = Parameter.Cost(Parameter.R);

                if (CountSameNumber == 30) {   //若解40次没有发生变化，则进行强制跳出（采用随机破坏和随机插入算子并直接接受）
                    //将new_Route[]清空
                    Calculation.ClearRoute(Parameter.new_Route);
                    //将原始线路克隆到new_Route[]中
                    Calculation.CloneRoute(Parameter.new_Route, Parameter.R);
                    //清空相关列表
                    for (int i = 0; i < Parameter.Remove_Node.length; i++) {
                        Parameter.Remove_Node[i] = new Node();
                    }
                    //采用随机算子
                    Destory.Random_Destory(Parameter.new_Route);
                    Repair.Random_Repair(Parameter.new_Route);
                    //直接更新结果
                    Calculation.ClearRoute(Parameter.R);
                    Calculation.CloneRoute(Parameter.R, Parameter.new_Route);
                    //计算更新后的Value
                    Parameter.New_Value = Parameter.Cost(Parameter.new_Route);
                    Parameter.Local_Value = Parameter.New_Value;
                    CountSameNumber = 0;        //完成后更新CountSameNumber
                } else if (CountSameNumber < 30) {      //若相同的次数不足50次
                    //将new_Route[]清空
                    Calculation.ClearRoute(Parameter.new_Route);
                    //将原始线路克隆到new_Route[]中
                    Calculation.CloneRoute(Parameter.new_Route, Parameter.R);
                    //清空相关列表
                    for (int i = 0; i < Parameter.Remove_Node.length; i++) {
                        Parameter.Remove_Node[i] = new Node();
                    }
                    //通过轮盘赌的方式选择破坏算子和修复算子
                    Calculation.selectSol_Roulette();
                    //采用选择的算子进行修复和破坏
                    Calculation.SelectAlgorithm();
                    //更新权重和得分
                    Calculation.CalScore();
                }

                T *= Down;
                System.out.println(Parameter.Best_Value);
                System.out.println(Parameter.Local_Value);
            }
            Iteration++;
            Calculation.ClearRoute(Parameter.R);
            Calculation.CloneRoute(Parameter.R, Parameter.Best_Route);
            System.out.println(Parameter.Best_Value);
            System.out.println("------------------------------");
        }
    }
}
