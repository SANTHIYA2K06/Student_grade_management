import React, { useState, useEffect } from 'react';
import { Layout, Table, Button, Modal, Form, Input, Select, message, Popover, Upload } from 'antd';
import { PlusOutlined, SearchOutlined, ReloadOutlined, FilterOutlined, UploadOutlined, DownloadOutlined } from '@ant-design/icons';
import api from "../api";
import './Contents.css';
import debounce from 'lodash/debounce';

const { Content } = Layout;
const { Option } = Select;

const AcademicStaffManagement = ({ setSelectedKey, readOnly = false }) => {
  const predefinedTitles = ['Professor', 'Associate Professor', 'Lecturer', 'Researcher', 'Staff'];
  const [staff, setStaff] = useState([]);
  const [filteredStaff, setFilteredStaff] = useState([]);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0,
  });
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [form] = Form.useForm();
  const [selectedStaff, setSelectedStaff] = useState(null);
  const [filterValues, setFilterValues] = useState({});
  const [departments, setDepartments] = useState([]);
  const [titles, setTitles] = useState([]);
  const [searchEntity, setSearchEntity] = useState('id');
  const [searchText, setSearchText] = useState('');

  // Add this function to reset filters
  const resetFilters = () => {
    setFilterValues({});
    fetchStaff({
      current: 1,
      pageSize: pagination.pageSize
    });
  };

  // Modify the fetchStaff function to include filter values
  const fetchStaff = async (params = {}) => {
    setLoading(true);
    try {
      const searchParams = {
        current: params.current || pagination.current,
        size: params.pageSize || pagination.pageSize,
        id: '',
        firstName: '',
        lastName: '',
        department: params.department || filterValues.department || '',
        title: params.title || filterValues.title || ''
      };

      // Handle search based on selected entity
      if (params.searchText) {
        const searchValue = params.searchText.trim();
        switch (params.searchEntity || searchEntity) {
          case 'id':
            searchParams.id = searchValue;
            break;
          case 'name':
            searchParams.firstName = searchValue;
            searchParams.lastName = searchValue;
            break;
          case 'title':
            searchParams.title = searchValue;
            break;
          case 'department':
            searchParams.department = searchValue;
            break;
          default:
            break;
        }
      }

      const response = await api.get('/api/staff/list-staffs', {
        params: searchParams
      });

      if (response.data?.data?.records) {
        setStaff(response.data.data.records);
        setFilteredStaff(response.data.data.records);
        setPagination({
          ...pagination,
          total: response.data.data.total,
          current: response.data.data.current,
          pageSize: response.data.data.size,
        });
      }
    } catch (error) {
      console.error('Error fetching staff:', error);
      message.error('Failed to load staff');
    } finally {
      setLoading(false);
    }
  };

  // Add function to fetch departments
  const fetchDepartments = async () => {
    try {
      const response = await api.get('/api/staff/list-departments');
      if (response.data?.code === 200) {
        setDepartments(response.data.data || []);
      }
    } catch (error) {
      console.error('Error fetching departments:', error);
      message.error('Failed to load departments');
    }
  };

  // Add function to fetch titles
  const fetchTitles = async () => {
    try {
      const response = await api.get('/api/staff/list-titles');
      if (response.data?.code === 200) {
        // Combine API titles with predefined ones and remove duplicates
        const allTitles = [...new Set([...predefinedTitles, ...(response.data.data || [])])];
        setTitles(allTitles);
      }
    } catch (error) {
      console.error('Error fetching titles:', error);
      // If API fails, fall back to predefined titles
      setTitles(predefinedTitles);
    }
  };

  useEffect(() => {
    fetchStaff();
    fetchDepartments();
    fetchTitles(); // Fetch titles when component mounts
  }, []);

  // Add staff
  const handleAddStaff = async () => {
    try {
      const values = await form.validateFields();
      setSubmitting(true);

      // Ensure title and department are strings, not arrays
      const formattedValues = {
        ...values,
        title: values.title?.toString(),
        department: values.department?.toString()
      };

      const response = await api.post("/api/staff/add-staff", formattedValues);

      if (response.data?.code === 200) {
        message.success('Academic staff added successfully');
        setIsModalOpen(false);
        form.resetFields();
        fetchStaff();
        setFilterValues({});
      } else {
        throw new Error(response.data?.message || 'Failed to add staff');
      }
    } catch (error) {
      console.error('Error adding staff:', error.response);
      message.error(`Failed to add staff: ${error.response?.data?.message || 'Unknown error'}`);
    } finally {
      setSubmitting(false);
    }
  };
 // Edit staff
  const handleEditStaff = async () => {
  try {
    const values = await form.validateFields();
    setSubmitting(true);
    await api.put(`/api/staff/edit/${selectedStaff.id}`, values);

    const updatedStaff = staff.map((member) =>
      member.id === selectedStaff.id ? { ...member, ...values } : member
    );
    setStaff(updatedStaff);
    setFilteredStaff(updatedStaff);
    message.success('Staff updated successfully');
    setIsEditModalOpen(false);
    form.resetFields();
  } catch (error) {
    console.error('Error editing staff:', error.response);
    message.error(`Failed to edit staff: ${error.response?.data?.message || 'Unknown error'}`);
  } finally {
    setSubmitting(false);
  }
  };

  // Delete staff
  const handleDeleteStaff = async () => {
    try {
      setSubmitting(true);
      await api.delete(`/api/staff/delete/${selectedStaff.id}`);

      const updatedStaff = staff.filter((member) => member.id !== selectedStaff.id);
      setStaff(updatedStaff);
      setFilteredStaff(updatedStaff);
      message.success('Staff deleted successfully');
      setIsDeleteModalOpen(false);
    } catch (error) {
      console.error('Error deleting staff:', error.response);
      message.error(`Failed to delete staff: ${error.response?.data?.message || 'Unknown error'}`);
    } finally {
      setSubmitting(false);
    }
  };

  const showEditModal = (record) => {
    if (record) {
      setSelectedStaff(record);
      form.setFieldsValue(record); // Populate the form with selected staff data
      setIsEditModalOpen(true);
    }
  };

  // Modify the filterContent to include the reset button
  const filterContent = (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
      <Select
        placeholder="Filter by Department"
        style={{ width: 200 }}
        value={filterValues.department}
        onChange={(value) => handleFilterChange('department', value)}
        allowClear
      >
        <Option value="">All Departments</Option>
        {departments.map((department) => (
          <Option key={department} value={department}>
            {department}
          </Option>
        ))}
      </Select>

      <Select
        placeholder="Filter by Title"
        style={{ width: 200 }}
        value={filterValues.title}
        onChange={(value) => handleFilterChange('title', value)}
        allowClear
      >
        <Option value="">All Titles</Option>
        {titles.map((title) => (
          <Option key={title} value={title}>
            {title}
          </Option>
        ))}
      </Select>

      <Button onClick={resetFilters} style={{ marginTop: '8px' }}>
        Reset Filters
      </Button>
    </div>
  );

  // Update the handleFilterChange function
  const handleFilterChange = (key, value) => {
    const updatedFilters = { ...filterValues, [key]: value };
    setFilterValues(updatedFilters);
    fetchStaff({ 
      ...pagination,
      current: 1,
      [key]: value
    });
  };
  const handleFileUpload = async ({ file, onSuccess, onError }) => {
    try {
      const formData = new FormData();
      formData.append('file', file);

      const response = await api.post('/api/staff/import-staffs', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      if (response.data?.code === 200) {
        message.success('Staff imported successfully');
        onSuccess('ok');
        fetchStaff();
      } else {
        throw new Error(response.data?.message || 'Import failed');
      }
    } catch (error) {
      console.error('Error importing staff:', error);
      message.error(error.response?.data?.message || 'Failed to import staff');
      onError(error);
    }
  };

  const handleSearch = debounce((value) => {
    setSearchText(value);
    fetchStaff({ 
      searchText: value,
      searchEntity: searchEntity,
      current: 1
    });
  }, 500);

  const csvTemplateContent = (
    <div style={{ maxWidth: '300px' }}>
      <p>Please download and follow the CSV template format for importing staff data:</p>
      <a 
        href="./files/StaffDataTemplate.csv" 
        download="Staff Data Template.csv"
        style={{ 
          color: '#1890ff',
          textDecoration: 'underline',
          display: 'flex',
          alignItems: 'center',
          gap: '8px'
        }}
      >
        <DownloadOutlined /> Download Template
      </a>
      <p style={{ marginTop: '8px', fontSize: '12px', color: '#666' }}>
        Note: The CSV file must match the template format exactly.
      </p>
    </div>
  );

  const columns = [
    {
      title: 'Staff ID',
      dataIndex: 'id',
      key: 'id',
      width: 100,
    },
    {
      title: 'First Name',
      dataIndex: 'firstName',
      key: 'firstName',
      width: 150,
    },
    {
      title: 'Last Name',
      dataIndex: 'lastName',
      key: 'lastName',
      width: 150,
    },
    {
      title: 'Email',
      dataIndex: 'email',
      key: 'email',
      render: (email) => email ? (
        <a href={`mailto:${email}`} style={{ color: '#1890ff' }}>
          {email}
        </a>
      ) : '-'
    },
    {
      title: 'Title',
      dataIndex: 'title',
      key: 'title',
      width: 200,
    },
    {
      title: 'Department',
      dataIndex: 'department',
      key: 'department',
      width: 200,
    }
  ];

  // Update Add/Edit Staff Modal Form
  const renderStaffForm = (
    <Form form={form} layout="vertical">
      <Form.Item name="firstName" label="First Name" rules={[{ required: true }]}>
        <Input />
      </Form.Item>
      <Form.Item name="lastName" label="Last Name" rules={[{ required: true }]}>
        <Input />
      </Form.Item>
      <Form.Item name="email" label="Email" rules={[{ required: true, type: 'email' }]}>
        <Input />
      </Form.Item>
      <Form.Item name="title" label="Title" rules={[{ required: true }]}>
        <Select
          showSearch
          style={{ width: '100%' }}
          placeholder="Select or type a title"
          optionFilterProp="children"
          filterOption={(input, option) =>
            (option?.children || '').toLowerCase().includes(input.toLowerCase())
          }
          onSearch={(value) => {
            if (value && !titles.includes(value)) {
              setTitles([...titles, value]);
            }
          }}
        >
          {titles.map((title) => (
            <Option key={title} value={title}>
              {title}
            </Option>
          ))}
        </Select>
      </Form.Item>
      <Form.Item name="department" label="Department" rules={[{ required: true }]}>
        <Select
          showSearch
          style={{ width: '100%' }}
          placeholder="Select or type a department"
          optionFilterProp="children"
          filterOption={(input, option) =>
            (option?.children || '').toLowerCase().includes(input.toLowerCase())
          }
          onSearch={(value) => {
            if (value && !departments.includes(value)) {
              setDepartments([...departments, value]);
            }
          }}
        >
          {departments.map((department) => (
            <Option key={department} value={department}>
              {department}
            </Option>
          ))}
        </Select>
      </Form.Item>
      <Form.Item
        name="username"
        label="Username"
        rules={[{ required: true, message: 'Please input a username!' }]}
      >
        <Input />
      </Form.Item>
      <Form.Item
        name="password"
        label="Password"
        rules={[
          { required: true, message: 'Please input a password!' },
          { min: 6, message: 'Password must be at least 6 characters!' },
        ]}
      >
        <Input.Password />
      </Form.Item>
    </Form>
  );

  // Add handleTableChange function
  const handleTableChange = (newPagination, filters, sorter) => {
    fetchStaff({
      current: newPagination.current,
      pageSize: newPagination.pageSize,
      searchText,
      ...filterValues
    });
  };

  return (
    <Content style={{ margin: '24px 16px 0', padding: 24, background: '#fff' }}>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between' }}>
        <h2>Academic Staff</h2>
        <div style={{ display: 'flex', gap: '8px' }}>
          <Button icon={<ReloadOutlined />} onClick={() => fetchStaff()} />
          <Input.Group compact>
            <Select
              defaultValue="id"
              value={searchEntity}
              onChange={setSearchEntity}
              style={{ width: 120 }}
            >
              <Option value="id">Staff ID</Option>
              <Option value="name">Name</Option>
              <Option value="title">Title</Option>
            </Select>
            <Input
              placeholder={`Search by ${searchEntity}`}
              prefix={<SearchOutlined />}
              onChange={(e) => handleSearch(e.target.value)}
              style={{ width: 250 }}
              allowClear
            />
          </Input.Group>
          <Popover content={<div>{filterContent}</div>} title="Filters" trigger="click">
                <Button icon={<FilterOutlined />}>Filters</Button>
              </Popover>
          {!readOnly && (
            <>

              <Popover 
                content={csvTemplateContent}
                title="CSV Template"
                trigger="hover"
                placement="bottom"
              >
              <Upload
                accept=".csv"
                showUploadList={false}
                customRequest={handleFileUpload}
                beforeUpload={(file) => {
                  const isCsv = file.type === 'text/csv' || file.name.endsWith('.csv');
                  if (!isCsv) {
                    message.error('You can only upload CSV files!');
                    return Upload.LIST_IGNORE;
                  }
                  return true;
                }}
              >
                <Button icon={<UploadOutlined />}>Import from CSV</Button>
              </Upload>
              </Popover>
              <Button type="primary" icon={<PlusOutlined />} onClick={() => setIsModalOpen(true)}>
                Add Staff
              </Button>
            </>
          )}
        </div>
      </div>

      <Table 
        columns={columns} 
        dataSource={filteredStaff} 
        loading={loading} 
        rowKey="id"
        pagination={{
          current: pagination.current,
          pageSize: pagination.pageSize,
          total: pagination.total,
          showSizeChanger: true,
        }}
        onChange={handleTableChange}
        onRow={(record) => ({
          onClick: () => {
            if (typeof setSelectedKey === 'function') {
              setSelectedKey(['staffDetail', record.id]);
            }
          },
          style: { cursor: 'pointer' }
        })}
        rowClassName={() => 'clickable-row'}
      />

      {/* Only show modals if not in read-only mode */}
      {!readOnly && (
        <>
          {/* Add Modal */}
          <Modal
            title="Add Academic Staff"
            visible={isModalOpen}
            onOk={handleAddStaff}
            onCancel={() => setIsModalOpen(false)}
            confirmLoading={submitting}
          >
            {renderStaffForm}
          </Modal>
          {/* Edit Modal */}
          <Modal
            title="Edit Academic Staff"
            visible={isEditModalOpen}
            onOk={handleEditStaff}
            onCancel={() => setIsEditModalOpen(false)}
            confirmLoading={submitting}
          >
            {renderStaffForm}
          </Modal>
          {/* Delete Confirmation Modal */}
          <Modal
            title="Confirm Delete"
            visible={isDeleteModalOpen}
            onOk={handleDeleteStaff}
            onCancel={() => setIsDeleteModalOpen(false)}
            okText="Delete"
            okButtonProps={{ danger: true }}
          >
            <p>Are you sure you want to delete this staff member?</p>
          </Modal>
        </>
      )}
    </Content>
  );
};

export default AcademicStaffManagement;
