package classes;

public class Individual {


    public Integer time;
    public  Integer fitness;
    public String chromosome[];


    /**
     *
     * @param chromosome
     */
    public Individual(String[] chromosome) {
        this.chromosome = chromosome;
    }

    /**
     *
     * @param chromosomeSize
     */
    public Individual(Integer chromosomeSize){
        this.chromosome = new String[chromosomeSize];
    }

    /***********************************Código gerado automaticamente pela IDE**************************************/

    public Integer getTime() {
        return time;
    }

    public Integer getFitness() {
        return fitness;
    }
}
