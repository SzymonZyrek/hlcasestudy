package edu.szyrek.hlcase.rest;

import edu.szyrek.hlcase.model.UserAccount;
import edu.szyrek.hlcase.repository.UserAccounts;
import jakarta.data.page.PageRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("useraccounts")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class UserAccountEndpoint {

    @Inject
    UserAccounts users;

    @GET
    public List<UserAccount> all(@QueryParam("pageSize") Integer size,
                                 @QueryParam("page") Integer page) {
        return size != null && page != null
                ? users.paged(PageRequest.ofPage(page).size(size)).content()
                : users.all();
    }

    @GET @Path("/{id}")
    public UserAccount get(@PathParam("id") Long id) {
        return users.findById(id).orElseThrow(() ->
                new WebApplicationException("User with id of " + id + " does not exist.", 404));
    }

    @POST
    public Response create(UserAccount user) {
        users.insert(user);
        return Response.status(201).build();
    }

    @PUT @Transactional
    public Response update(UserAccount input) {
        UserAccount user = get(input.getId());
        if (input.getAge() != null) user.setAge(input.getAge());
        if (input.getGender() != null) user.setGender(input.getGender());
        users.update(user);
        return Response.noContent().build();
    }

    @DELETE @Path("/{id}") @Transactional
    public Response delete(@PathParam("id") Long id) {
        users.delete(get(id));
        return Response.noContent().build();
    }
}
