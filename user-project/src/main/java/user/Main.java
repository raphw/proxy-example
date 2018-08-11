package user;

import container.ProxyFactory;

public class Main {

    public static void main(String[] args) {
        SampleBean sampleBean = new ProxyFactory().proxy(new SampleBean());

        sampleBean.hello();
    }

}
