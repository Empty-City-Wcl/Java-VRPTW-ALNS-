public class OutPut {
    public static void Output() {//结果输出
        System.out.println("************************************************************");
        System.out.println("The Minimum Total Distance = " + Parameter.Best_Value);
        System.out.println("Concrete Schedule of Each Route as Following : ");

        int M = 0;
        for (int i = 1; i <= Parameter.VehicleNumber; ++i)
            if (Parameter.Best_Route[i].N.size() > 2) {
                M++;
                System.out.print("No." + M + " : ");

                for (int j = 0; j < Parameter.Best_Route[i].N.size() - 1; ++j)
                    System.out.print(Parameter.Best_Route[i].N.get(j).ID + " -> ");
                System.out.println(Parameter.Best_Route[i].N.get(Parameter.Best_Route[i].N.size() - 1).ID);
            }
        System.out.println("************************************************************");
    }

}