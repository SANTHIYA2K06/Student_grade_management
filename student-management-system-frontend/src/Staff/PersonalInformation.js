import React, {useEffect, useState} from 'react';
import {Avatar, Button, Card, Col, Layout, message, Row, Statistic, Table, Typography} from 'antd';
import {
  BankOutlined,
  IdcardOutlined,
  MailOutlined,
  ReloadOutlined,
  TeamOutlined,
  UserOutlined
} from '@ant-design/icons';
import api from '../api';
import './Contents.css';
import {useNavigate} from 'react-router-dom';

const {Content} = Layout;
const {Title, Text} = Typography;


const PersonalInformation = ({setSelectedKey}) => {  // Add setSelectedKey as a prop here
  const navigate = useNavigate();
  const [staff, setStaff] = useState(null);
  const [loading, setLoading] = useState(false);

  const [modules, setModules] = useState([]);
  const [moduleStats, setModuleStats] = useState([]);

  const fetchStaffData = async () => {
    setLoading(true);
    try {
      const response = await api.get(`/api/staff/detail`);
      if (response.data?.code === 200) {
        const staffData = response.data.data;
        setStaff(staffData);

        // Fetch modules for this specific staff using their name
        const modulesResponse = await api.get('/api/module/list', {
          params: {
            current: 1,
            size: 100,
            staffFirstName: staffData.firstName,
            staffLastName: staffData.lastName
          }
        });

        // Filter modules to only include those where this staff is the leader
        const moduleRecords = modulesResponse.data?.data?.records || [];
        const filteredModules = moduleRecords.filter(module =>
            module.staffFirstName === staffData.firstName &&
            module.staffLastName === staffData.lastName
        );
        setModules(filteredModules);

        // Fetch statistics for each module
        const statsPromises = filteredModules.map(module =>
            api.get('/api/record', {
              params: {
                moduleCode: module.code,
                current: 1,
                size: 100
              }
            })
        );

        const statsResponses = await Promise.all(statsPromises);
        const moduleStatistics = statsResponses.map((response, index) => {
          const records = response.data?.data?.records || [];
          const totalScore = records.reduce((sum, record) => sum + (record.averageScore || 0), 0);
          const totalPassRate = records.reduce((sum, record) => sum + (record.passRate || 0), 0);
          const totalCandidates = records.reduce((sum, record) => sum + (record.numberOfCandidates || 0), 0);

          return {
            moduleCode: filteredModules[index].code,
            averageScore: records.length ? totalScore / records.length : 0,
            passRate: records.length ? totalPassRate / records.length : 0,
            studentCount: totalCandidates
          };
        });
        setModuleStats(moduleStatistics);
      }
    } catch (error) {
      message.error('Failed to fetch personal information');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const handleModuleClick = (moduleCode) => {
    setSelectedKey(['moduleDetail', moduleCode]);
  };

  // Calculate overall statistics
  const calculateStatistics = () => {
    if (!moduleStats.length) return null;

    const totalModules = moduleStats.length;
    const totalStudents = moduleStats.reduce((sum, m) => sum + (m.studentCount || 0), 0);
    const averageScore = moduleStats.reduce((sum, m) => sum + (m.averageScore || 0), 0) / totalModules;
    const averagePassRate = moduleStats.reduce((sum, m) => sum + (m.passRate || 0), 0) / totalModules;

    return {
      totalModules,
      totalStudents,
      averageScore: averageScore.toFixed(1),
      averagePassRate: (averagePassRate * 100).toFixed(1)
    };
  };

  useEffect(() => {
    const handleAuthError = (event) => {
      message.error(event.detail.message);
      navigate('/login');
    };

    window.addEventListener('auth-error', handleAuthError);
    fetchStaffData();

    return () => {
      window.removeEventListener('auth-error', handleAuthError);
    };
  }, [navigate]);

  const InfoItem = ({ icon, label, value, span = 12 }) => (
    <Col span={span}>
      <Card bordered={false} style={{ height: '100%' }}>
        <div style={{display: 'flex', alignItems: 'center', gap: '12px'}}>
          <div style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            width: '32px',
            height: '32px',
            borderRadius: '6px',
            background: '#f0f5ff'
          }}>
            {icon}
          </div>
          <div>
            <Text type="secondary" style={{fontSize: '13px'}}>{label}</Text>
            <div style={{
              fontSize: '14px',
              fontWeight: '500',
              marginTop: '2px',
              color: value ? '#000000d9' : '#00000040'
            }}>
              {value || 'Not Available'}
            </div>
          </div>
        </div>
      </Card>
    </Col>
  );

  if (!staff) {
    return (
      <Content className="student-record">
        <div style={{ marginBottom: 12, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Title level={4} style={{ margin: 0 }}>Personal Information</Title>
          <Button icon={<ReloadOutlined />} onClick={fetchStaffData} loading={loading} />
        </div>
        <Card loading={true} />
      </Content>
    );
  }

  return (
    <Content className="student-record">
      <div style={{ marginBottom: 12, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Title level={4} style={{ margin: 0 }}>Personal Information</Title>
        <Button icon={<ReloadOutlined />} onClick={fetchStaffData} loading={loading} />
      </div>

      {/* Profile Header */}
      <Card style={{ marginBottom: 20 }}>
        <div style={{display: 'flex', alignItems: 'center', gap: '20px'}}>
          <Avatar
              size={64}
              icon={<UserOutlined/>}
              style={{
                backgroundColor: '#1890ff',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }}
          />
          <div>
            <Title level={4} style={{margin: 0}}>
              {staff.title} {staff.firstName} {staff.lastName}
            </Title>
            <Text type="secondary" style={{ fontSize: '14px' }}>
              {staff.username}
            </Text>
          </div>
        </div>
      </Card>

      {/* Basic Information */}
      <Title level={5} style={{ marginBottom: 12, marginTop: 0 }}>Basic Information</Title>
      <Row gutter={[12, 12]} style={{ marginBottom: 20 }}>
        <InfoItem
            icon={<MailOutlined style={{color: '#1890ff', fontSize: '16px'}}/>}
            label="Email"
            value={staff.email}
            span={24}
        />
        <InfoItem
            icon={<IdcardOutlined style={{color: '#52c41a', fontSize: '16px'}}/>}
            label="Staff ID"
            value={staff.id}
        />
        <InfoItem
            icon={<UserOutlined style={{color: '#722ed1', fontSize: '16px'}}/>}
            label="Title"
            value={staff.title}
        />
      </Row>

      {/* Department Information */}
      <Title level={5} style={{ marginBottom: 12, marginTop: 0 }}>Department Information</Title>
      <Row gutter={[12, 12]} style={{ marginBottom: 24 }}>
        <InfoItem
            icon={<BankOutlined style={{color: '#eb2f96', fontSize: '16px'}}/>}
            label="Department"
            value={staff.department}
            span={24}
        />
        <InfoItem
            icon={<TeamOutlined style={{color: '#fa8c16', fontSize: '16px'}}/>}
            label="Role"
            value={staff.role || 'Academic Staff'}
        />
      </Row>

      {/* Teaching Modules Section */}
      <Title level={5} style={{ marginBottom: 12, marginTop: 0 }}>Teaching Modules</Title>
      <Table
        columns={[
          {
            title: 'Module Code',
            dataIndex: 'code',
            key: 'code',
            width: 120
          },
          {
            title: 'Module Name',
            dataIndex: 'name',
            key: 'name',
            width: 250
          },
          {
            title: 'Credits',
            dataIndex: 'credits',
            key: 'credits',
            width: 100
          },
          {
            title: 'MNC',
            dataIndex: 'mnc',
            key: 'mnc',
            width: 100,
            render: (mnc) => mnc ? 'Yes' : 'No'
          },
          {
            title: 'Students',
            key: 'studentCount',
            width: 120,
            render: (_, record) => {
              const stats = moduleStats.find(s => s.moduleCode === record.code);
              return stats?.studentCount || 0;
            }
          },
          {
            title: 'Average Score',
            key: 'averageScore',
            width: 120,
            render: (_, record) => {
              const stats = moduleStats.find(s => s.moduleCode === record.code);
              return stats?.averageScore ? `${stats.averageScore.toFixed(1)}%` : '-';
            }
          },
          {
            title: 'Pass Rate',
            key: 'passRate',
            width: 120,
            render: (_, record) => {
              const stats = moduleStats.find(s => s.moduleCode === record.code);
              return stats?.passRate ? `${(stats.passRate * 100).toFixed(1)}%` : '-';
            }
          }
        ]}
        dataSource={modules}
        rowKey="code"
        onRow={(record) => ({
          onClick: () => handleModuleClick(record.code),
          style: { cursor: 'pointer' }
        })}
        rowClassName={() => 'clickable-row'}
        pagination={false}
        loading={loading}
      />

      {/* Teaching Statistics Section - Now after the module list */}
      {modules.length > 0 && calculateStatistics() && (
        <Card title="Teaching Overview" style={{ marginTop: 24 }}>
          <Row gutter={[16, 16]}>
            <Col span={6}>
              <Card bordered={false} style={{ background: '#f0f5ff' }}>
                <Statistic
                  title="Total Modules"
                  value={calculateStatistics().totalModules}
                  valueStyle={{ color: '#1890ff' }}
                />
              </Card>
            </Col>
            <Col span={6}>
              <Card bordered={false} style={{ background: '#f6ffed' }}>
                <Statistic
                  title="Total Students"
                  value={calculateStatistics().totalStudents}
                  valueStyle={{ color: '#52c41a' }}
                />
              </Card>
            </Col>
            <Col span={6}>
              <Card bordered={false} style={{ background: '#fff0f6' }}>
                <Statistic
                  title="Average Score"
                  value={calculateStatistics().averageScore}
                  suffix="%"
                  valueStyle={{ color: '#eb2f96' }}
                />
              </Card>
            </Col>
            <Col span={6}>
              <Card bordered={false} style={{ background: '#f0f2ff' }}>
                <Statistic
                  title="Average Pass Rate"
                  value={calculateStatistics().averagePassRate}
                  suffix="%"
                  valueStyle={{ color: '#722ed1' }}
                />
              </Card>
            </Col>

          </Row>
        </Card>
      )}
    </Content>
  );
};

export default PersonalInformation;
