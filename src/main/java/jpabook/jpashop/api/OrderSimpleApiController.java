package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.OrderSimpleQueryDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne(ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public Result orderV2() {
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<OrderSimpleQueryDto> collect = orders.stream()
                .map(OrderSimpleQueryDto::new)
                .collect(Collectors.toList());

        return new Result(collect);
    }

    @GetMapping("/api/v3/simple-orders")
    public Result orderV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<OrderSimpleQueryDto> collect = orders.stream()
                .map(OrderSimpleQueryDto::new)
                .collect(Collectors.toList());
        return new Result(collect);
    }

    @GetMapping("/api/v4/simple-orders")
    public Result orderV4() {
        return new Result(orderRepository.findOrderDtos());
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        List<T> data;
    }

}
